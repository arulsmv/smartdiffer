#!/bin/bash

sudo cd /opt

wget http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz

sudo tar -xvzf apache-maven-3.3.9-bin.tar.gz
sudo mv apache-maven-3.3.9 maven 

echo "export M2_HOME=/opt/maven" > /etc/profile.d/mavenenv.sh
echo "export PATH=${M2_HOME}/bin:${PATH}"  >>/etc/profile.d/mavenenv.sh

sudo chmod +x /etc/profile.d/mavenenv.sh
sudo source /etc/profile.d/mavenenv.sh


