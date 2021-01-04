


# Represents a node in a NoCloud grid.
#
module NoCloud

	class Node

		# * +storage_root_path+ - A directory or symlink to a directory which contains the files to store.
		attr_accessor :storage_root_path

		# * +identifier+ - The unique identifier for the local node in the grid.
		attr_accessor :identifier

		def initialize(identifier, storage_root_path = '/storage/')
			@identifier = identifier
			@storage_root_path = storage_root_path
		end

	end
end