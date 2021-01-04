
## Application 

The application uses the local endpoint (which is no-cloud plus the hardware)
to distribute data to other endpoints. 

## Grid

## Node

From the perspective of the Grid, it can either be a local or a remote node.
The local node always holds the primary copy of data, while the remote nodes
act as trusted storage.

The application can then check this with
  
```ruby
valid=endpoint.isValid(filename,allowed_time_since_last_checked)
```

It will only return true if the checksums are correct AND when these checksums have been computed
within the allowed time frame.

## Connection

Defines the transport layer used for accessing a specific remote node.
