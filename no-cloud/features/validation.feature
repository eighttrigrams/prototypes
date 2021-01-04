Feature: Validation helps to make sure the data stored onto different endpoints cannot change unnoticed.
In order to make sure that a client can ask the system that its data has not been altered
an endpoint
should mark incoming data with checksums as well as be able to ensure that the same gets done for its remotely stored data.

# Axioms:
# There is no proof of correctness for the foreign checksum. This is entirely trust based
# The local checksum is set to INVALID if recomputed and not equal to the one computed before TODO
# The remote nodes always set the checksum to the newly computed value whithout comparison
#
Background:
Given there is a local endpoint configured with a storage location at resources/features/common/storage/
  And there is a remote endpoint at 127.0.0.1 reachable via port 2225 named remoteEndpoint1
  And the applications has a file resources/features/common/abc.txt which contains the string "original"
  And the application asks the endpoint to distribute the file as abc.txt.logical

Scenario: The checksums have not been gathered yet
Then the endpoint should report the data is "invalid"

Scenario: The remote checksums have not been calculated yet
Then the endpoint should report the data is "invalid"

Scenario: The local copy gets deleted after it has been computed correct previously
Given the data gets checksummed
 When the local copy of the file gets destroyed
 Then the endpoint should report the data is "invalid"

# Background: The original files content is "original". With this content the file gets checksummed.
# Note: Important to not forget example 3!
Scenario Outline: Data corruption
When the content of the local file consists of the string "<localFileContent>"
 And the content of the remote file consists of the string "<remoteFileContent>"
 And the data gets checksummed
Then the endpoint should report the data is "<validity>" 
Examples: 
  | localFileContent | remoteFileContent | validity |
  | original         | original          | valid    |
  | original         | modified          | invalid  |
  | modified         | original          | invalid  |
  | modified         | modified          | invalid  |

Scenario Outline: Checksum of remote copies up to date or not
Given the data gets checksummed
 When the time checkums are considered valid is "<checksumValidityPeriod>" seconds
  And we wait "<elapsedTime>" seconds without gathering the remote checksums
 Then the endpoint should report the data is "<validity>" 
Examples:
  | checksumValidityPeriod | elapsedTime | validity    |
  | 1                      | 3           | invalid     |
  | 10                     | 1           | valid       |

