#!/bin/bash

sudo apt-get update

sudo apt-get install default-jre

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update

sudo apt-get install oracle-java8-installer

#sudo update-alternatives --config java

sudo echo JAVA_HOME="/usr/lib/jvm/java-8-oracle" >> /etc/environment

source /etc/environment

