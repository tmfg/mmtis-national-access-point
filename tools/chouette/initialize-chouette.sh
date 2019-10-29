#!/bin/bash

# Current chouette version - change if version changes
chouettezip=mobi.chouette.command-3.4.8.zip
# Folder where unzipped content is moved
chouettezipFolder=chouette-cmd_3.4.8
# Folder where napote tries to use chouette. Configured in config.edn
destinationdir=/opt/chouette

echo "1. Create $destinationdir folder"
if [ ! -d "$destinationdir" ]; then

  sudo mkdir -p "$destinationdir"
  response=$?
  if [ $response -eq 0 ]; then
      echo "OK"
  else
      echo "FAIL"
  fi

else
  echo "Directory $destinationdir exists."
fi

echo "2. Copy $chouettezip to $destinationdir"
sudo cp $chouettezip $destinationdir
if [ $? -eq 0 ]; then
    echo "Copy - OK"
else
    echo "Copy - FAIL"
fi

echo "3. Unzip $chouettezip"
sudo cd "$destinationdir"
sudo unzip "$destinationdir"/$chouettezip -d "$destinationdir"

echo "4. Give permissions "
currentuser=$(who | awk 'NR==1{print $1}')
sudo cd ..
sudo chown -R $currentuser:staff "$destinationdir"
sudo chmod -R 777 "$destinationdir"

echo "5. Remove .zip"
rm -rf "$destinationdir"/$chouettezip

echo "6. Copy files to $destinationdir"
sudo mv "$destinationdir"/$chouettezipFolder/* "$destinationdir"/
sudo rm -rf "$destinationdir"/$chouettezipFolder/
echo "READY!"
