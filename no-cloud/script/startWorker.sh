#!/bin/bash

# author: Daniel M. de Oliveira

source /usr/local/rvm/scripts/rvm

pid=`ps -ef | grep "lib/no_cloud/worker.rb" | grep -v grep | awk '{print $2;}'`
if [ "$pid" != "" ]; then
	echo already running
	exit
fi

nohup ruby lib/no_cloud/worker.rb `hostname` /storage /nocloud/db &