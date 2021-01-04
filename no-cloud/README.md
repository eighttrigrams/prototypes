no-cloud
========

NoCloud is a proof of concept for an infrastructure layer framework designed
for cloud storage applications. 
The NoCloud architecture should help building an managing networks of collaborating endpoints 
which store data with mutual trust. NoCloud is written in Ruby.

## Building no-cloud

### Dependencies

Must be installed on your development workstation

* ruby
* vagrant
* virtualbox

Please install the following gems before you start:
* rspec
* cucumber

### Build

Download the repository. In preparation of building the library, from the root directory 
run the following commands

```bash
cd no-cloud
cd vagrant 
ssh-add key
vagrant up
cd ..
```
This will create three virtual machines running ubuntu precise pangolin 64bit. It is done 
with [vagrant](https://www.vagrantup.com/) on top of [virtualbox](https://www.virtualbox.org/).

After that you can build the library with
```bash
script/build.sh
```

First off this will run the unit tests (using rspec). If they pass the script will
provision the three vms with the newest build of the no-cloud library. 
Doing so ensures that the component and acceptance tests run against a fully operational
network of no-cloud endpoints. 

The component tests make use of [rspec](http://rspec.info/) and the acceptance tests make use of [cucumber](https://cukes.info/). If all of them pass
the build script informs the user that the build was a success.

## Sample Application

This paragraph will give you a quick glance on how NoCloud can be used to build an application.

```ruby
localNode=Node.new 'myHost'
remoteNode1=Node.new 'remoteHost1'
remoteNode2=Node.new 'remoteHost2'

grid= Grid.new localNode,'/storage/db'
connection1= RsyncSshStorageConnector.new USER1, HOST1, PORT1
connection2= RsyncSshStorageConnector.new USER2, HOST2, PORT2
grid.addNode connection1, remoteNode1
grid.addNode connection2, remoteNode2
grid.startDistributionWorker
grid.startChecksumWorker
```	
	
Ingest then will work like this

```ruby
grid.distribute '/tmp/myfile.txt', 'myfile.txt.logicalfilename'
```

Later you can check if your data is valid, which means it is distributed to all endpoints for which
storage connectors were defined for. and the checksums are correct and not older than a specified amount
of time (in seconds).	
	
```ruby
isValid=grid.isValid 'myfile.txt.logicalfilename',7200
```

Note that, depending on the configuration of the network as a whole, it may take some time until
the checkums for the remote copies get calculated remotely and gathered locally for the first time.

If you want to access your data, ask your local endpoint by calling

```ruby
grid.fetch('myfile.txt.logicalfilename','/tmp/myfile.is.back.txt')
```

## More information

For more information visit our [documentation](docs/index.md)
