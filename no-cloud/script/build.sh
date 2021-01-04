#!/bin/bash


## functions

function cleanup {
  rm vagrant/startWorker.sh > /dev/null
  rm vagrant/stopWorker.sh > /dev/null
  rm -rf vagrant/lib > /dev/null
}

## unit tests

rspec
if [ $? = 1 ] 
then
  echo unit tests not passed
  cleanup
  exit 1
fi

## component tests

#TODO



## deploy binaries


if [ -e "vagrant/lib" ]; then rm -r vagrant/lib; fi
cp -r lib vagrant/
cp -f script/startWorker.sh vagrant/startWorker.sh
cp -f script/stopWorker.sh  vagrant/stopWorker.sh

cd vagrant
vagrant provision
cd ..


## acceptance tests


cucumber
if [ $? = 1 ]
then
  echo acceptance tests not passed
  cleanup
  exit 1
fi

cleanup

echo build successful




