#!/bin/bash

# author: Daniel M. de Oliveira

pid=`ps -ef | grep "lib/no_cloud/worker.rb" | grep -v grep | awk '{print $2;}'`
if [ "$pid" != "" ]; then
 echo kill $pid
 kill $pid
 rm logfile
fi