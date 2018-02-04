#!/bin/bash

yes | sudo apt-get install python-pip python-dev build-essential 
yes | sudo pip install --upgrade pip 
yes | sudo pip install --upgrade virtualenv 

sudo pip install GitPython

