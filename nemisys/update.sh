#!/bin/bash

cd ..
./compile.sh
cd nemisys/

#TODO: save version
echo "Fetching latest Nemisys jar from Jenkins..."
wget https://ci.nukkitx.com/job/NukkitX/job/Nemisys/job/master/lastSuccessfulBuild/artifact/target/nemisys-1.0-SNAPSHOT.jar

echo "Replacing existing jar..."
rm nemisys.jar
mv nemisys-1.0-SNAPSHOT.jar nemisys.jar
echo "Update complete!"
