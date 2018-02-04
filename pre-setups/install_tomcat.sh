#!/bin/bash

curr_dir=$(dirname $0)

sudo groupadd tomcat
sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat

cd /tmp
curl -O http://apache.mirrors.ionfish.org/tomcat/tomcat-8/v8.5.5/bin/apache-tomcat-8.5.5.tar.gz
sudo mkdir /opt/tomcat
sudo tar xzvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

cd /opt/tomcat
sudo chgrp -R tomcat /opt/tomcat


sudo chmod -R g+r conf
sudo chmod g+x conf


sudo chown -R tomcat webapps/ work/ temp/ logs/


sudo cp ${curr_dir}/tomcat.service /etc/systemd/system/tomcat.service

sudo systemctl daemon-reload
sudo systemctl start tomcat


sudo systemctl status tomcat

sudo ufw allow 8080
sudo systemctl enable tomcat

#sudo nano /opt/tomcat/conf/tomcat-users.xml
#<tomcat-users . . .>
#    <user username="admin" password="password" roles="manager-gui,admin-gui"/>
#</tomcat-users>


#----
#for manager app
#sudo nano /opt/tomcat/webapps/manager/META-INF/context.xml

#for host manager app
#sudo nano /opt/tomcat/webapps/host-manager/META-INF/context.xml

#Comment out ip restrictions
#<Context antiResourceLocking="false" privileged="true" >
#  <!--<Valve className="org.apache.catalina.valves.RemoteAddrValve"
#         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />-->
#</Context>

sudo systemctl restart tomcat
