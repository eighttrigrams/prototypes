# The endpoint to the grid which provides
# services for an application to manage its clients
# data in a safe and distributed manner.
#
# Facade. 
#
# ==== Author
#
# Daniel M. de Oliveira
#
module NoCloud

	class Grid

		CMD_SEPARATOR = ','
		HYPHEN = '-'
		EMPTY = ''
		MATCH_FILE_NOT_RECORD = "%{fn}(?!\.record$)" # +fn+ - filename
		MD5='md5'
		INVALID_MARKER = 'INVALID'
		COMMAND_DISTRIBUTE = 'DISTRIBUTE'

		# Creates an instance of NoCloudRoot. Note that, though singleton usage is not enforced, only
		# one instance of NoCloudRoot should get created per application.
		#
		# ==== Attributes
		# * +local_node+ - Represents the local node consisting of a storage location and a database for managing the grid's clients data.
		# * +dbLocation+ - A file system path to a directory where the database can place its data. The database hosts information about which primary copies the endpoint hosts and on which endpoints they are stored.
		#
		def initialize(local_node, dbLocation)
			local_node.storage_root_path<<FS_SEPARATOR unless local_node.storage_root_path.end_with?(FS_SEPARATOR)
      @local_node=local_node
      
			checkFoldersExist
      
			@connections=Hash.new
			@remote_nodes=Hash.new
			@db=DataManagement::MetadataRecordManager.new dbLocation
			@todoList=DataManagement::PersistentStringQueue.new dbLocation
			@drm=DataManagement::DatastoreRecordManager.new local_node.identifier,local_node.storage_root_path
		end



		# Attaches an endpoint for hosting secondary copies of the data applications request NoCloudRoot to store.
		#
		# ==== Attributes
		#
		# +storageConnector+ - An instance of a storage connector which encapsulates the actual transfer protocol which handles all data transfers between the local endpoint and the specific endpoint.
		# +remote_node+ - Unique identifier for the remote endpoint within the network of endpoints.
		#
		def addNode(storageConnector,remote_node)
			raise NoMethodError, 'Missing one ore more methods. is not a storage connector' if not is_connector? storageConnector

			storageConnector.setStorageLocation(remote_node.storage_root_path)
			@connections[remote_node.identifier]=storageConnector
			@remote_nodes[remote_node.identifier]=remote_node
		end


		# Starts a thread which tries to gather all remote checksums for the
		# file which has not been checked for the longest time.
		#
		# For each run one file is selected.
		# The runner then iterates over each and every file the local endpoint stores a copy of and tries to get the
		# corresponding metadata record from each of the connected endpoints in order to incorporate it
		# into the local database.
		#
		# Iterates over all local copies and compares their actual checksums to the ones
		# stored within the database. If the actual checksum and the stored checksums are not equal, the database
		# entry gets updated to mark the copy as INVALID
		#
		def startChecksumWorker()

			return if ((defined? @checksumWorkerOnHalt) && @checksumWorkerOnHalt==false)
			@checksumWorkerOnHalt=false

			Thread.new{

				while @checksumWorkerOnHalt==false do
					sleep 1
					logicalFileName=@db.file_name_oldest_record
					next if logicalFileName == nil

					gather_chksums(logicalFileName)
				end
			}
		end

		def haltChecksumWorker()
			@checksumWorkerOnHalt=true
		end


		def startDistributionWorker()

			return if ((defined? @distributionWorkerOnHalt) && @distributionWorkerOnHalt==false)
			@distributionWorkerOnHalt=false

			Thread.new{

				while @distributionWorkerOnHalt==false do
					sleep 0.1
					todoItem=@todoList.dequeue
					next if todoItem.nil?

					transfered=@connections[endpointIdentifier(todoItem)].transfer @local_node.storage_root_path+@local_node.identifier+FS_SEPARATOR+logicalFileName(todoItem), @local_node.identifier+FS_SEPARATOR+logicalFileName(todoItem)

					if not transfered then
						@todoList.enqueue assembleDistributionCmd(logicalFileName(todoItem),endpointIdentifier(todoItem))
					end
				end
			}
		end

		def haltDistributionWorker()
			@distributionWorkerOnHalt=true
		end



		# Puts a file into the storage grid and triggers its replication
		# to all the available endpoints.
		#
		# The call to distribute is partly asynchronous. This means
		# that the method returns after the file has been transfered but
		# doesn't mean the file has been replicated to all available endpoints.
		#
		# @return true if file is stored locally. false otherwise.
		#
		# ==== Attributes
		#
		# +sourceFileName+ - denotes a file. The file must exist. The sourceFileName must not point to a directory.
		# +logicalFileName+ - when stored into the network of endpoints, the file is given a logical name which acts as a unique identifier for the file. By this identifier files can get accessed or checked for validity
		#
		def distribute(sourceFileName,logicalFileName)
			raise NoCloud::Errors::FileNotFoundError, sourceFileName if ! File.exist?(sourceFileName)

			md5 = Digest::MD5.file(sourceFileName).hexdigest
			@db.set_orig_chksum logicalFileName, md5

			FileUtils.cp(sourceFileName,@local_node.storage_root_path+@local_node.identifier+FS_SEPARATOR+logicalFileName)

			@connections.each do |hostIdentifier,connector|
				@todoList.enqueue assembleDistributionCmd(logicalFileName,hostIdentifier)
			end

			return true
		rescue Errno::ENOENT => e
			metadataFile.close unless not defined? metadataFile
			return false
		end


		# Iterates over each and every file the local endpoints hold in custody
		# and generates an new checksum for the file. A metadata record with the checksum gets created
		# as a sidecar file to the original file which gets placed there for getting harvested by the endpoint
		# which holds the primary copy. In case the record already exists, the checksum gets replaced by the newly
		# created one. In both cases a current timestamp gets inserted to indicate the last time the local node has
		# last checked the file.
		#
		def refreshStorageChecksums
      @drm.update_storage(@local_node.identifier)
		end




		# Gets a stored file from the network and makes a copy locally
		#
		# @param storedFileName must not contain any delimiters.
		# @param targetFilePath absolute or relative path where the copy is
		# expected to get created.
		#
		def fetch(logicalFileName,targetFilePath)

			if ((not (ownedFile logicalFileName).nil?) and (File.exists? (ownedFile logicalFileName)))
			then
				FileUtils.cp (ownedFile logicalFileName), targetFilePath
				return
			end



			@connections.each do |hostIdentifier,connector|
				if connector.exist? @local_node.identifier+FS_SEPARATOR+logicalFileName then
					result=connector.fetch @local_node.identifier+FS_SEPARATOR+logicalFileName,targetFilePath
					return if result==true
				end
			end
			raise NoCloud::Errors::FileNotFoundError, logicalFileName


		rescue Errno::ENOENT => e
			raise NoCloud::Errors::FileNotFoundError, targetFilePath
		end


		# Lets a client application check if the data is valid. That a copy of a file is valid
		# in this context means that it has a checksum which is the same as the checksum originally generated
		# and that this checksum is not older than specified by +checksumTimeout+.
		#
		# In order to give a sure answer, all copies, the local endpoint must have a checksum for
		# local copy and one file for each remote endpoint configured. So one must make sure (by calling gather and
		# refreshLocalChecksums) that all available information gets collected before checking for validity.
		#
		def isValid(logicalFileName,checksumTimeout)
			return false if (ownedFile logicalFileName).nil?

			return false if not @db.hasUpToDateChecksumForHost?(@local_node.identifier,logicalFileName,checksumTimeout)
			localChecksum= @db.getChecksum(@local_node.identifier,logicalFileName)
      return false if localChecksum != @db.get_orig_chksum(logicalFileName)


			@connections.each do |hostIdentifier,storageConnector|

				return false if not @db.hasUpToDateChecksumForHost?(hostIdentifier,logicalFileName,checksumTimeout)

				remoteChecksum=@db.getChecksum(hostIdentifier,logicalFileName)
        return false if remoteChecksum != @db.get_orig_chksum(logicalFileName)
			end

			return true
		end


		private

		def assembleDistributionCmd(logicalFileName,endpointIdentifier)
			COMMAND_DISTRIBUTE+CMD_SEPARATOR+endpointIdentifier+CMD_SEPARATOR+logicalFileName
		end

		def logicalFileName(todoItem)
			todoItem.split(CMD_SEPARATOR)[2]
		end

		def endpointIdentifier(todoItem)
			todoItem.split(CMD_SEPARATOR)[1]
		end

		def checkFoldersExist
			raise NoCloud::Errors::FileNotFoundError, @local_node.storage_root_path+@local_node.identifier+FS_SEPARATOR if not File.exist?(@local_node.storage_root_path+@local_node.identifier+FS_SEPARATOR)
		end

		def is_connector?(storageConnector)
			fulfilled=true
			fulfilled=false unless storageConnector.respond_to? :transfer
			fulfilled=false unless storageConnector.respond_to? :fetch
			fulfilled=false unless storageConnector.respond_to? :exist?
			fulfilled=false unless storageConnector.respond_to? :getFileMetadata
			return fulfilled
		end

		def gather_chksums(logical_file_name)

      local_record=@drm.get_record @local_node.identifier,logical_file_name
      if not local_record.nil? then
        @db.incorporateEntry(local_record,logical_file_name)
      end
			@connections.each do |hostIdentifier,storageConnector|

				remote_record=storageConnector.getFileMetadata @local_node.identifier+FS_SEPARATOR+logical_file_name

				if not remote_record == EMPTY then

					@db.incorporateEntry(remote_record,logical_file_name)
				end
			end
		end

		def ownedFile(logicalFileName)
			Dir[@local_node.storage_root_path+@local_node.identifier+FS_SEPARATOR+'*'].select { |f| f =~ /#{MATCH_FILE_NOT_RECORD % {fn:logicalFileName}}/}.last
		end
	end
end
