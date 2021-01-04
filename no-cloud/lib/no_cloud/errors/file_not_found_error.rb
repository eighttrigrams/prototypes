module NoCloud
	module Errors
		class FileNotFoundError < StandardError
			def initialize(path)
				super ERR_MSG_FILE_NOT_FOUND + path
			end
		end
	end
end