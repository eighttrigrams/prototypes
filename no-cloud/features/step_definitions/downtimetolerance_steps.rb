Given(/^the host named (.*) gets disconnected$/) do |hostName|
  system 'cd vagrant; vagrant halt '+hostName+'; cd ..'
end

Given(/^after waiting another (.*) seconds the host named (.*) comes up again$/) do |sleepTime, hostName|
  puts 'sleep '+sleepTime
  sleep sleepTime.to_i
  system 'cd vagrant; vagrant up '+hostName+'; cd ..'
  sleep 2 # a little time for distributing
end