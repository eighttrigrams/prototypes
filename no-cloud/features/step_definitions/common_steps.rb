require_relative '../../spec/spec_helper' #TODO: extract helpers

Given(/^there is a local endpoint configured with a storage location at (.*)$/) do |storageLocation|
  @dbLocation='resources/features/common/db'

  n=NoCloud::Node.new('devHost', storageLocation)

  @r=NoCloud::Grid.new n,@dbLocation
  @storageLocation=storageLocation+'devHost/'
  @r.startDistributionWorker
  @r.startChecksumWorker
end

Given(/^there is a remote endpoint at (.*) reachable via port (.*) named (.*)$/) do |host,port,hostIdentifier|
  n=NoCloud::Node.new(hostIdentifier, '/storage/')

  connection=NoCloud::Connectors::RsyncSshStorageConnector.new 'vagrant',host,port

  @r.addNode connection, n
  @endpoints[port]=host
end

Given(/^the applications has a file (.*) which contains the string "(.*?)"$/) do |sourceFilePath,content|
  writeFile(sourceFilePath,content)
  @sourceFilePath=sourceFilePath
end


When(/^the application asks the endpoint to distribute the file as (.*)$/) do |logicalFileName|
  @logicalFileName=logicalFileName
  @r.distribute @sourceFilePath,logicalFileName
  sleep 1 # some time to distribute to all remote locations
end

def destroy_remote_copies()
  @endpoints.each do |port,host|
    cmd='ssh -p '+port+' vagrant@'+host+" 'if [ -e /storage/devHost/"+@logicalFileName+" ]; then rm /storage/devHost/"+@logicalFileName+"; fi'"
    a=`#{cmd}`
    cmd='ssh -p '+port+' vagrant@'+host+" 'if [ -e /storage/devHost/"+@logicalFileName+".record ]; then rm /storage/devHost/"+@logicalFileName+".record ; fi'"
    a=`#{cmd}`
  end
end

Before do
  @endpoints=Hash.new
end

After do
  @r.haltChecksumWorker
  destroy_remote_copies
  
  File.delete @dbLocation+@logicalFileName+'.record' if File.exist? @dbLocation+@logicalFileName+'.record'
  File.delete @storageLocation+@logicalFileName+'.record' if File.exist? @storageLocation+@logicalFileName+'.record'
  File.delete @dbLocation+'persistentstringqueue.csv' if File.exist? @dbLocation+'persistentstringqueue.csv'
  File.delete @dbLocation+'checksums.csv' if File.exist? @dbLocation+'checksums.csv'
  File.delete @storageLocation+@logicalFileName if File.exist? @storageLocation+@logicalFileName
  File.delete @retrievedFilePath if defined? @retrievedFilePath and File.exist? @retrievedFilePath
  File.delete @sourceFilePath if File.exist? @sourceFilePath
end

