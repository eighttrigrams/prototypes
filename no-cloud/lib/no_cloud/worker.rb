#TODO: move
require_relative '../no_cloud'


storageLocation=''
storageLocation<<ARGV[1]
hostName=''
hostName<<ARGV[0]


n=NoCloud::Node.new(hostName, storageLocation)


r=NoCloud::Grid.new n,'/nocloud/db'

while true do
  logfile=File.new 'logfile', 'w'
  logfile.puts 'running'
  logfile.close
  r.refreshStorageChecksums
  sleep 1
end