# @author: Daniel M. de Oliveira
#

module NoCloud
	module Connectors

		class RsyncSshStorageConnector

			attr_reader :hostName, :portNr

			def initialize(userName,hostName,portNr)
				@userName=userName
				@hostName=hostName
				@portNr=portNr
			end

			# TODO remove
			def setStorageLocation(storageLocation)
				@storageLocation=storageLocation
			end

			# @param logicalFilePath must not contain any delimiters.
			# @param targetFilePath absolute or relative path where the copy is
			# expected to get created.
			#
			def fetch(logicalFilePath,targetFilePath)

				transfered=system "rsync -e 'ssh -o batchmode=yes -p "+ @portNr+"' -aq "+@userName+'@'+@hostName+':'+@storageLocation+'/'+logicalFilePath +' '+targetFilePath

				raise StandardError if ! transfered
				true
			end

			# Returns true if transfer was successful
			def transfer(sourceFilePath,logicalFilePath)
				raise RuntimeError, 'source file '+sourceFilePath+' not found' if ! File.exist? sourceFilePath

				transfered=system "rsync -e 'ssh -o batchmode=yes -p "+ @portNr+"' -aq "+sourceFilePath+' '+@userName+'@'+@hostName+':'+@storageLocation+'/'+logicalFilePath+' 2>/dev/null'

				return transfered
			end

			def exist?(logicalFilePath)
				cmd='ssh -p '+@portNr+' '+@userName+'@'+@hostName+" 'if [ -e "+ @storageLocation+'/'+logicalFilePath+" ]; then printf true; else printf false; fi' 2>/dev/null"

				result=`#{cmd}`
				if (result=='true') then
					return true
				else
					return false
				end
			end

			def getFileMetadata(logicalFilePath)
				if not exist? logicalFilePath+'.record' then
					return ''
				end

				cmd='ssh -p '+@portNr+' '+@userName+'@'+@hostName+" 'if [ -e "+ @storageLocation+'/'+logicalFilePath+'.record ]; then cat '+ @storageLocation+'/'+logicalFilePath+".record; fi'"
				a=`#{cmd}`

				return a.gsub("\n",'') # TODO test line break removal
			end
		end
	end
end