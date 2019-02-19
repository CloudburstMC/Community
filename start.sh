#!/bin/bash

while true
do
	echo "Starting Nukkit server for $1..."
	./update.sh
	cd $1

	#create symlinks for shared plugin files
	cd plugins/
	ln -s ../../shared/plugins/* .
	cd ..

	#start server
	java -Xmx1G -XX:+UseG1GC -jar ../nukkit.jar
	cd ..
	echo "Press Ctrl+C to stop"
	sleep 3
done
