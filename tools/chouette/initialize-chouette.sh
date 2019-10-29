#!/bin/bash

echo "1. Create /opt/chouette/ folder"
if [ ! -d "/opt/chouette/" ]; then

  sudo mkdir -p /opt/chouette
  response=$?
  if [ $response -eq 0 ]; then
      echo "OK"
  else
      echo "FAIL"
  fi

else
  echo "Directory /opt/chouette/ exists."
fi

echo "2. Copy chouette.zip to /opt/chouette"
sudo cp mobi.chouette.command-3.4.8.zip /opt/chouette/
if [ $? -eq 0 ]; then
    echo "Copy - OK"
else
    echo "Copy - FAIL"
fi

echo "3. Unzip chouette.zip"
sudo cd /opt/chouette
sudo unzip /opt/chouette/mobi.chouette.command-3.4.8.zip -d /opt/chouette/

echo "4. Give permissions "
sudo cd ..
sudo chown -R markusva:staff /opt/chouette
sudo chmod -R 777 /opt/chouette

echo "5. Remove .zip"
rm -rf /opt/chouette/mobi.chouette.command-3.4.8.zip

echo "6. Copy files to /opt/chouette"
sudo mv /opt/chouette/chouette-cmd_3.4.8/* /opt/chouette/
sudo rm -rf /opt/chouette/chouette-cmd_3.4.8/
echo "READY!"
