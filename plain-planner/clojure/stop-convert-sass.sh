#!/bin/bash

PROCID=`ps -fx | grep sassc | grep -v grep | awk '{print \$2}'`
[ ! -z "$PROCID" ] && kill $PROCID || echo "No procid found"