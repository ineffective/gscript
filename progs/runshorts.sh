#!/bin/bash

exec 3<&1

while read x ; do echo ; echo 'RUNNING: '"$x" ; echo 'RUNNING: '"$x" >&3; gsr +g +o "$x" ; echo ; done < shorts >result 2>&1

