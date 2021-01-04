

Then(/^the endpoint should report the local copy is valid$/) do
  expect(@r.localCopyIsValid(@logicalFileName)).to be true
end



When(/^the content of the remote file consists of the string "(.*?)"$/) do |alteration|
  @endpoints.each do |port,host|
    cmd='ssh -p '+port+' vagrant@'+host+" 'echo "+alteration+' > /storage/devHost/'+@logicalFileName+"'"
    a=`#{cmd}`
  end
end

When(/^the content of the local file consists of the string "(.*)"$/) do |alteration|
  writeFile(@storageLocation+@logicalFileName,alteration)
end

When(/^the data gets checksummed/) do
  @r.refreshStorageChecksums
  @endpoints.each do |port,host|
    cmd='ssh -p '+port+' vagrant@'+host+" 'cd /nocloud; ./stopWorker.sh; ./startWorker.sh; sleep 2; ./stopWorker.sh'"
    a=`#{cmd}`
  end
  sleep 2
end


When(/^the time checkums are considered valid is "(.*?)" seconds$/) do |checksumValidityPeriod|
  @checksumValidityPeriod=checksumValidityPeriod.to_i
end

When(/^we wait "(.*?)" seconds without gathering the remote checksums$/) do |elapsedTime|
  @r.haltChecksumWorker
  puts 'sleep '+elapsedTime
  sleep elapsedTime.to_i
  @r.startChecksumWorker
end

Then(/^the endpoint should report the data is "(.*?)"$/) do |validity|
  @checksumValidityPeriod=DEFAULTCHECKSUMTIMEOUT if @checksumValidityPeriod.nil? 
  validityShould=true if validity=='valid'
  validityShould=false if validity=='invalid'
  expect(@r.isValid(@logicalFileName,@checksumValidityPeriod)).to be validityShould
end


