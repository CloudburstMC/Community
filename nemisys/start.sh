#!/bin/bash

while true
do
	echo "Starting Nemisys server..."
	./update.sh
	java -Xmx512M -XX:+UseG1GC -jar nemisys.jar
	echo "Press Ctrl+C to stop"
	sleep 3
done
