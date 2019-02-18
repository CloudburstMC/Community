#!/bin/bash

#if git pull | grep -q 'Already up-to-date.'; then
#	echo "Not compiling."
#else
echo "Compiling plugins!"
cd plugins/
./gradlew build
cd ..
#fi

echo "Setting plugin symlinks..."

ln -s ../../plugins/proxy/build/libs/proxy-0.0.1.jar nemisys/plugins/proxy.jar
