#!/bin/bash


PROCID=`ps -fx | grep sassc | grep -v grep | awk '{print \$2}'`
[ ! -z "$PROCID" ] && kill $PROCID


fswatch -0 src/scss/ | xargs -0 -n 1 -I {} sassc src/scss/styles.scss resources/public/css/styles.css

