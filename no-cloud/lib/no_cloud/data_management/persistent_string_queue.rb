# A database which stores strings.
#
# author: Daniel M. de Oliveira
#

module NoCloud
	module DataManagement
		class PersistentStringQueue

			DBFILENAME='persistentstringqueue.csv'

			# If not already existent, creates a database file whitin the directory dbRoot.
			#
			def initialize(dbRoot)
				dbr=dbRoot.gsub(/\/$/,'')
				raise ArgumentError, NoCloud::Errors::ERR_MSG_SHOULD_BE_DIRECTORY+dbr if File.file? dbr
				raise ArgumentError, NoCloud::Errors::ERR_MSG_FILE_NOT_FOUND+dbRoot if not File.exist? dbr

				@dbRootDir=dbr+'/'
				FileUtils.touch databaseFilePath if not File.exist? databaseFilePath

				lock :off
			end

			# Puts an entry to the queue.
			def enqueue(entry)
				sleep 0.1 while lock==:active
				lock :on

				entries=readFile
				entries<<entry
				writeFile(entries)

				lock :off
			end

			# Gets the entry that hast been put first.
			# Removes the entry from the queue.
			def dequeue
				sleep 0.1 while lock==:active
				lock :on

				lines=readFile
				if lines.count==0 then
					lock :off
					return nil
				end

				first,rest=cutRest lines
				writeFile(rest)

				lock :off
				return stripNewline first
			end


			private

			def stripNewline(param)
				return param.gsub(/\n$/,'')
			end

			def lock(state=:default)
				raise StandardError if state != :off and state != :on and state != :default
				@locked = true if state == :on
				@locked = false if state == :off
				if state == :default then
					return :active if @locked==true
					return :off if @locked==false
				end
			end

			# Returns the first line and the rest
			def cutRest(lines)
				isFirst=true
				rest=[]
				first=''

				lines.each do |line|
					if isFirst==true
						isFirst=false
						first=line
					else
						rest<<line
					end
				end

				return first,rest
			end

			def databaseFilePath
				@dbRootDir+DBFILENAME
			end

			# Writes an array of lines to a file.
			# If the file exists, overwrites it.
			#
			def writeFile(lines)
				databaseFile=File.new databaseFilePath, 'w'
				lines.each do |line|
					databaseFile.puts line
				end
				databaseFile.close
			end

			# returns an array with the lines
			#
			def readFile
				databaseFile=File.new databaseFilePath, 'r'
				content=[]
				databaseFile.each {|line|
					content << line
				}
				databaseFile.close
				content
			end
		end
	end
end