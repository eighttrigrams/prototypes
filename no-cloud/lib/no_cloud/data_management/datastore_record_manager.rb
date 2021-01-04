module NoCloud
	module DataManagement


    # Provides methods to create and manage metadata records for
    # the files of a datastore. These metadata records are suitable for
    # getting processed by the metadata_record_manager.
    #
    # The datastore which can hold data for the local host as well as for
    # remote hosts has the structure
    #
    # storage_root
    #   hostIdentifier1
    #     same_data.txt
    #   hostIdentifier2
    #     some_data.txt
    #
    # ==== Author
    #
    # Daniel M. de Oliveira
    #
		class DatastoreRecordManager

			FS_SEPARATOR='/'
      MD5='md5'
      EMPTY = ''
			SEPARATOR=','
			RECORD_SFX='.record'
      MATCH_FILES_NOT_RECORDS = /^(.(?<!\.record))*?$/


			def initialize(local_node_identifier,storage_root_path)
				if not File.exist? storage_root_path then raise RuntimeError end

				storage_root_path<<FS_SEPARATOR unless storage_root_path.end_with? FS_SEPARATOR
				@dbRoot=storage_root_path
        @local_node_identifier=local_node_identifier
			end

			def add_checksum(host_name,logicalFileName,checksumType,checksum)
				if not File.exist? @dbRoot+host_name then raise RuntimeError end

				lines=[]
				lines[0]=Time.now.to_s+SEPARATOR+@local_node_identifier+SEPARATOR+logicalFileName+SEPARATOR+checksumType+SEPARATOR+checksum
				writeFile(host_name,logicalFileName,lines)
      end



      # Get a metadata record for the file +logical_file_name+ stored
      # for host +host_name+.
      #
      # ==== Return
      # nil if metadata record does not exist.
      #
      def get_record(host_name,logical_file_name)
        return nil if not File.exist? recordFilePath(host_name,logical_file_name)

        content=readFile(host_name,logical_file_name)
        return content[0] unless content.size==0
        return nil
      end



      # Updates the metadata record for every stored file.
      # If the record does not exist, create one.
      #
      def update_storage(local_node_identifier)
        stored_files.each do |filePath|

          hostIdentifier=filePath.gsub(@dbRoot,EMPTY).gsub(FS_SEPARATOR+File.basename(filePath),EMPTY)
          md5 = Digest::MD5.file(filePath).hexdigest
          add_checksum(hostIdentifier,File.basename(filePath),MD5,md5)
        end
      end


      private


      def stored_files
        Dir[@dbRoot+'*/*'].select { |f| f =~ MATCH_FILES_NOT_RECORDS}
      end

			# writes an array of lines to a file
			def writeFile(hostIdentifier,logicalFileName,lines)
				recordFile=File.new recordFilePath(hostIdentifier,logicalFileName), 'w'
				lines.each do |line|
					recordFile.puts line
				end
				recordFile.close
			end

			# returns an array with the lines
			#
			def readFile(hostIdentifier,logicalFileName)
				recordFile=File.new recordFilePath(hostIdentifier,logicalFileName), 'r'
				content=[]
				recordFile.each {|line|
					content << line
				}
				recordFile.close
				content
			end

			def recordFilePath(hostIdentifier,logicalFileName)
				return @dbRoot+hostIdentifier+FS_SEPARATOR+logicalFileName+RECORD_SFX
			end
		end
	end
end