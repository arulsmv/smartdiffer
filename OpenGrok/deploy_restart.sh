#!/bin/bash


export OPENGROK_TOMCAT_BASE=/opt/tomcat/

script_dir=$(dirname $0)
script_file=$(basename $0)

${script_dir}/OpenGrok deploy

#sudo service restart tomcat
systemctl restart tomcat 

