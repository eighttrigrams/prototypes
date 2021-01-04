

Then(/^the file is present at the local storage location/) do
  expect(File.exist? @storedFilePath).to be true
end

Then(/^the file is present on the file system of the remote machine (.*) reachable via port (.*)$/) do |host,port|
  cmd='ssh -p '+port+' vagrant@'+host+" 'ls -l /tmp/'"
  a=`#{cmd}`
  expect(a.include? @logicalFileName).to be true
end

When(/^the copy at the remote endpoint (.*) port (.*) gets destroyed$/) do |host,port|
  cmd='ssh -p '+port+' vagrant@'+host+" 'rm /tmp/"+@logicalFileName+"'"
  a=`#{cmd}`
end

When(/^the local copy of the file gets destroyed$/) do
  File.delete @storageLocation+@logicalFileName if File.exists? @storageLocation+@logicalFileName
end

When(/^the remote copies of the file get destroyed$/) do
  destroy_remote_copies()
end

Then(/^the file can be retrieved to the location (.*)$/) do |retrievedFilePath|
  @r.fetch(@logicalFileName,retrievedFilePath)
  expect(File.exist? retrievedFilePath).to be true
  @retrievedFilePath=retrievedFilePath
end

When(/^the application moves its file afterwards$/) do
  File.delete @sourceFilePath if File.exist? @sourceFilePath # simulate the move. applications should not do this  
end







