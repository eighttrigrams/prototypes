require 'time'

# author: Daniel M. de Oliveira
#

module NoCloud
	module DataManagement

		class MetadataRecordManager

			FS_SEPARATOR='/'
			SEPARATOR=','
			SUFFIX='.record'
      CHECKSUMFILE='checksums.csv'

			def initialize(dbRootDir)
				if dbRootDir.nil? then raise RuntimeError end
				if not File.exist? dbRootDir then raise RuntimeError end
				if File.file? dbRootDir then raise RuntimeError end

				dbRootDir<<FS_SEPARATOR unless dbRootDir.end_with?(FS_SEPARATOR)
				@dbRootDir=dbRootDir
			end

      def set_orig_chksum(logical_file_name,chksum)
        createIfNotExist logical_file_name
        orig_checksums=File.new @dbRootDir+CHECKSUMFILE, 'a+'
        orig_checksums.puts(logical_file_name+','+chksum)
        orig_checksums.close
      end

      def get_orig_chksum(logical_file_name)
        orig_checksums=File.new @dbRootDir+CHECKSUMFILE, 'r'
        content=[]
        orig_checksums.each {|line|
          content << line
          if line.include? logical_file_name then
            orig_checksums.close
            return line.split(',')[1].gsub(/\n$/,'')
          end
        }
        orig_checksums.close
        return nil
      end

			# IMPORTANT: does not change the date of the entry
			# return true if incororated successfully, false otherwise
			#
			def incorporateEntry(entry,logicalFileName)
				createIfNotExist logicalFileName

				valid= entry =~ /^\d{4}[-]\d{2}[-]\d{2}\s\d{2}[:]\d{2}[:]\d{2}\s[+]\d{4}[,].*[,].*[,]\w*[,]\w*$/
				if valid.nil? then return false end

				# extract host identifier
				hostIdentifier=entry.split(/,/)[1]

				oldContent=readFile(logicalFileName)
				newContent=updateContent oldContent,hostIdentifier,entry

				writeFile(logicalFileName,newContent)
				true
			end

			def hasUpToDateChecksumForHost?(hostIdentifier,logicalFileName, maxAge ) # in seconds
				hostEntry=getChecksumForHost(hostIdentifier, logicalFileName)

				if (hostEntry.nil?) then
					return false
				end

				timeForHost=Time.parse hostEntry.split(/,/)[0]
				if (Time.now-timeForHost)<maxAge then
					return true
				end

				false
			end

			def getChecksum(hostIdentifier,logicalFileName)
				hostEntry=getChecksumForHost(hostIdentifier, logicalFileName)
				if (hostEntry.nil?) then
					raise RuntimeError
				end

				content=readFile(logicalFileName)
				hostEntry=getHostEntry(content,hostIdentifier)
				return hostEntry.split(/,/)[4].gsub("\n",'')
			end


      # The oldest record is the one which has the oldest
      # modified time attribute.
      #
			def file_name_oldest_record

				oldestRecord=Dir[@dbRootDir+'*record'].sort_by{ |f| File.mtime(f) }.last

				return nil if oldestRecord==nil
				oldestRecord.gsub(@dbRootDir,'').gsub(SUFFIX,'')
			end


			private


			# return nil if not existent
			#
			def getChecksumForHost(hostIdentifier,logicalFileName)
				if not File.exist? recordFilePath(logicalFileName) then
					return nil
				end

				content=readFile(logicalFileName)
				hostEntry=getHostEntry(content,hostIdentifier)
				return hostEntry
			end

			# return nil if does not find
			#
			def getHostEntry(content,hostIdentifier)
				containsHostEntry=false

				content.each {|line|
					if line.include? hostIdentifier then
						return line
					end
				}
				return nil
			end

			# returns an array of lines as the the new content
			#
			def updateContent(oldContent,hostIdentifier,updatedLineForHostIdentifier)
				newContent=[]
				nrUpdates=0
				oldContent.each do |line|
					if line.include? hostIdentifier then
						newContent<<updatedLineForHostIdentifier
						nrUpdates=nrUpdates+1
					else
						newContent<<line
					end
				end

				if nrUpdates==0 then
					newContent<<updatedLineForHostIdentifier
				end
				return newContent
			end

			# writes an array of lines to a file
			def writeFile(logicalFileName,lines)
				recordFile=File.new recordFilePath(logicalFileName), 'w'
				lines.each do |line|
					recordFile.puts line
				end
				recordFile.close
			end

			# returns an array with the lines
			#
			def readFile(logicalFileName)
				recordFile=File.new recordFilePath(logicalFileName), 'r'
				content=[]
				recordFile.each {|line|
					content << line
				}
				recordFile.close
				content
			end

			def recordFilePath(logicalFileName)
				return @dbRootDir+logicalFileName+SUFFIX
			end

			def createIfNotExist(logicalFileName)
				if not File.exist? recordFilePath(logicalFileName) then
					FileUtils.touch recordFilePath(logicalFileName)
				end
			end
		end
	end
end