Feature: Downtime tolerance makes the the system as a whole robust against downtimes of endpoints.
In order ensure that the distribution and validation features function as designed with some endpoints disconnected
an endpoint
should be able to defer tasks such as distributing of data and collecting of checksums.

Background:
Given there is a local endpoint configured with a storage location at resources/features/common/storage/
  And there is a remote endpoint at 127.0.0.1 reachable via port 2225 named remoteEndpoint1
  #And there is a remote endpoint at 127.0.0.1 reachable via port 2226 named remote_endpoint_2 # TODO does not work because all nodes mark their checksums as remote_entpoint_1
  And the applications has a file resources/features/common/abc.txt which contains the string "original"
  
  
Scenario:
Given the host named remoteEndpoint1 gets disconnected
  And the application asks the endpoint to distribute the file as abc.txt.logical
  And after waiting another 2 seconds the host named remoteEndpoint1 comes up again
  And the data gets checksummed
 Then the endpoint should report the data is "valid"