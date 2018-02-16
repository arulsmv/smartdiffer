#!/bin/bash

dir_name=$(dirname $0)
sudo mkdir -p /mnt/production/
sudo cp -r ${dir_name}/../neo4j /mnt/production/ 

sudo mkdir -p /mnt/production/neo4j/data/log
sudo chown neo4j:neo4j /mnt/production/neo4j/data
sudo yes | /mnt/production/neo4j/bin/neo4j-installer install
sudo /mnt/production/neo4j/bin/neo4j start-no-wait

