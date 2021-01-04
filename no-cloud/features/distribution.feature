Feature: Distributing a file to different locations.
In order to make sure that data survives the destruction on one location
an endpoint
should store the data locally as well as replicate it to other endpoints.

Background:
Given there is a local endpoint configured with a storage location at resources/features/common/storage/
  And there is a remote endpoint at 127.0.0.1 reachable via port 2225 named remoteEndpoint1
  And there is a remote endpoint at 127.0.0.1 reachable via port 2226 named remoteEndpoint2
  And there is a remote endpoint at 127.0.0.1 reachable via port 2227 named remoteEndpoint3
  And the applications has a file resources/features/common/abc.txt which contains the string "original"
  And the application asks the endpoint to distribute the file as abc.txt.logical

Scenario: The local copy gets destroyed
When the local copy of the file gets destroyed
Then the file can be retrieved to the location resources/features/common/abc.txt.retrieved
  
Scenario: All remote copies get destroyed
When the remote copies of the file get destroyed
Then the file can be retrieved to the location resources/features/common/abc.txt.retrieved

Scenario: Two remote copies and the local copy get destroyed
When the local copy of the file gets destroyed
 And the copy at the remote endpoint 127.0.0.1 port 2225 gets destroyed
 And the copy at the remote endpoint 127.0.0.1 port 2227 gets destroyed
Then the file can be retrieved to the location resources/features/common/abc.txt.retrieved

#Scenario: When all copies got destroyed
#When all copies get destroyed
#Then TODO