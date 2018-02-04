#!/bin/bash

cd /tmp/
mkdir install-ctags
cd install-ctags
git clone https://github.com/universal-ctags/ctags.git
cd ctags
./autogen.sh
./configure 
make 
sudo make install
cd /tmp/
rm -rf install-ctags

