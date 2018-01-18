#replacing numbers in in vim s/\d\d\t\n//gc
export EDITOR=vim
#Dont use PATH=$PATH:new
export PATH="/usr/local/bin:/usr/local/opt/coreutils/libexec/gnubin:${HOME}/Downloads/scala-2.10.3:${HOME}/bin/play-2.2.1/:/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin:/Applications/Xcode.app/Contents/Developer/usr/bin:/Library/Frameworks/Python.framework/Versions/2.7/bin:${HOME}/thirdparty/hadoop-0.20.205.0/bin:/Users/name/Apps/bin/protobuf-2.0.3/bin:/bin:/opt/local/bin:/sbin:/usr/X11/bin:/usr/bin:${HOME}/.ccm/repository/1.2.8/bin:/usr/local/bin:/usr/local/git/bin:/usr/sbin:${HOME}/bin:${HOME}/maven/apache-maven-3.0.5/bin:${HOME}/apache-cassandra-1.2.15/bin"
export MANPATH="/usr/local/opt/coreutils/libexec/gnuman:$MANPATH"
export SENSEI_HOME=${HOME}projects/sensei/sensei-1.5.0

tunnelto() {
  ssh -A arul@host.com -D8000 -L *:$2:$1:$2
}

alias cqlsh=${HOME}/.ccm/repository/1.2.8/bin/cqlsh
alias localping="ping 192.168.0.1"
alias bping="ping 255.255.255.255"
alias google="ping google.com"
#ant testit -Dtest.set=**/logtailer/*Test.class
export ANT_OPTS="-Xms256m -Xmx512m"
export MAVEN_OPTS="-Xmx1024m"
export M2_HOME=/usr/share/maven/
export DIST=${HOME}/work/src/backend/dist/
export HADOOP_CLASSPATH=${HOME}/work/src/backend/dist/lib/jets3t-0.8.1a.jar
export HADOOP_USER_CLASSPATH_FIRST=true
export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64/jre"
function android(){
  export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64/jdk"
}
#export JAVA_HOME=$(/usr/libexec/java_home)
export EC2_PRIVATE_KEY=~/.ec2/pk-amazon.pem
export EC2_CERT=~/.ec2/cert-amazon.pem
export DYLD_LIBRARY_PATH=/usr/local/mysql/lib

#Add mysql bin
export CPP=/Applications/Xcode.app/Contents/Developer/usr/bin/cpp
export LDFLAGS=-L/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.7.sdk/usr/lib
export CPPFLAGS="-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.7.sdk/usr/include/ -I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.7.sdk/usr/include/c++/4.2.1/"
export CFLAGS=-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.7.sdk/usr/include

alias gs="git status"
alias gshow="git show"
alias amend="git commit -a --amend"
alias commit="git commit -a"
alias ga="git add"
alias gb="git branch"
alias gf="git fetch"
alias gp="git pull origin master"
alias prepush="~/scripts/prepush.sh"
alias gpush="git push origin master"
alias gdm="git diff origin/master"
alias grb="git rebase -i origin/master"
alias gl="git log"
alias gt="git tag -l"
alias gc="git checkout"
alias greset="git reset --hard origin/HEAD"

export OPENGROK_TOMCAT_BASE=/opt/tomcat/
cpf() {
  cp $2 $1
}

gd() {
  branch=$(git status | grep "On branch " | cut -f 3 -d' ')
  date=$(date +"%Y%m%d.%H")
  mv $HOME/Desktop/Diff/$branch.diff $HOME/Desktop/Diff/$branch.diff.${date}
  git diff origin/master > $HOME/Desktop/Diff/$branch.diff
  less $HOME/Desktop/Diff/$branch.diff
  touch $HOME/Desktop/Diff/$branch.diff
}


lsdiff() {
  branch=$(git status | grep "On branch " | cut -f 3 -d' ')
  ls -l ~/Desktop/Diff/${branch}.diff*
}

lessdiff() {
  if [[ $# -ne 1 ]]; then
    branch=$1
  else
    branch=$(git status | grep "On branch " | cut -f 3 -d' ')
  fi
  less ~/Desktop/Diff/${branch}.diff
}

sshu() {
 ssh ubuntu@$1
}


#### From Previous
#alias fu="fileutil"
alias alog="vi /mnt/production/static-server/access-logs/access.log"
alias ..='cd ..'
alias ...='cd ../..'
alias up3='cd ../../..'
alias up4='cd ../../../..'
alias up5='cd ../../../../..'
alias up6='cd ../../../../../..'
alias cp='cp -ivp'
alias rm='rm -i'
alias fixkey='metacity --replace &'
alias g5='/google/data/ro/projects/shelltoys/g5.sar'
alias sortk2="sort -k2 -t$'\t' -n -r " 
alias gchrome="nohup $HOME/bin/gachrome.sh &"
export GOOGLE_USE_CORP_SSL_AGENT=true
export HISTSIZE=100000
export HISTFILESIZE=100000

alias hg="history | egrep"
#Bash should append rather than overwrite the history
shopt -s histappend


#Ignore small typos in directory names
shopt -s cdspell

shopt -s dotglob

#Ignore case when completing
set completion-ignore-case on

#Move in history using up and down arrows
#bind "\e[A": history-search-backward
#bind "\e[B": history-search-forward

#On tab show completion
set show-all-if-ambiguous on

#source /etc/bash_completion

function grephist() { grep "${*}" ${HOME}/jotlog;}
#When displaying the prompt, write the previous line to disk
#PROMPT_COMMAND='history -a ; 
export PROMPT_COMMAND='history -a ; echo `tty``date``pwd` `history 1`  >> ${HOME}/jotlog'

export GFS_CLIENT_SECURITY_LEVEL="integrity"
alias fixImports="/home/build/nonconf/google3/tools/java/remove_unused_imports.py --fix -c"

#PS1="\n\e[37; 44m[\h]\w\e[0m\n$ "

complete -o nospace -C "gqui COMPLETE" gqui

#Quick use of commands
alias vibs='vi ~/.bashrc && source ~/.bashrc'
alias knownhosts="vi ~/.ssh/known_hosts"
alias bashrc='source ~/.bashrc'
alias grepbs='cat ~/.bashrc | grep'
alias gopen="g4 opened | cut -f 1 -d '#' | cut -f 5- -d '/'"
alias clnum="g4 opened | grep -v default | cut -f 5 -d ' ' | sort -u"
alias lsg="ls | grep"
alias psg="ps auxwww | grep"
alias getenv="printenv | grep "
gjotlog() {
  grep -i $1 ${HOME}/jotlog
}
tjotlog() {
 lines=${1:-10}
 tail -${lines} ${HOME}/jotlog
}
proxychrome() {
  proxy=$1
  /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --user-data-dir=${HOME}/.config/proxy/ --proxy-server="http=${proxy}" >& /tmp/proxy.log &
}
#export http_proxy=http://cache.corp.google.com:3128
##### s4cmd specifics
alias sls="source ~/.bashrc; s4cmd ls "
sgetto() {
  s4cmd -f $3 get $1 $2
}
sget() {
  mkdir ~/tmp/data/$2
  file_name=$(s4cmd -f get $1 ~/tmp/data/$2 | grep $1 | cut -f 2 -d'>')
  vi $file_name
}
sgetr() {
  s3file=$1
  basename=$(basename ${s3file:4})
  mkdir ~/tmp/tmp/${basename}
  s4cmd -r -f get ${s3file} ~/tmp/tmp/${basename}
}

function jdes(){
  elastic-mapreduce --describe $1 | grep State
}
jdescribe() {
  elastic-mapreduce --describe $1 | less
}
alias jter="elastic-mapreduce --terminate "
alias emr="elastic-mapreduce "
#######################################################################################################################################
## Git shortcuts cmds
alias gitrevertall="git status | grep modified | cut -f 2 -d':' | xargs git co"
alias gcmt="git commit -a -m "
alias gexpo="git5 export -d "
ce() {
  export gcmd="git commit --all --message \"$1\""
  echo ${gcmd}
  ${gcmd}
  if [ $? -ne 0 ]; then
    echo "commit failed"
    return 225
  fi
  /usr/bin/git5 export -d \"$1\"
}

gcm() {
  gbranch=$(git status | grep "On branch " | cut -f 3 -d' ')
  gstatus=$(git status | grep "nothing to commit" | wc -l)
  read -n2 -p "Working on the branch '$gbranch'. Continue? (y/n)? "
  [[ "$REPLY" != "y" ]] && return 0

  if (( $gstatus != 1 ));then
    git commit -a 
    if (( $? !=0 )); then
      echo "Commit failed. Exiting."
      return 1
    fi
  fi
  git checkout master
}

abspath() {
  ls -l ${PWD}/$1
}
fu() {
  us=$1
  shift
  cmd=$1
  shift
  remaining=$*
  myfu_cmd="fileutil --gfs_user=${us} ${cmd} ${remaining}"
  echo $myfu_cmd
  $myfu_cmd
}
alias fudev="fu codeview-dev"

alias fudir="fileutil --gfs_user=codeview-dev  ls -l "
alias fudirl="fileutil --gfs_user=codeview-dev  ls -l /cns/mh-d/home/codeview-dev/user-data/trace/| tail -1 | awk '{print \$8}' | xargs fileutil --gfs_user=codeview-dev  ls -l"

fucp() {
  dirname=$1
  rm -f /tmp/trace.hrf
  cmd="fileutil --gfs_user=codeview-dev cp -f /gfs/mh/home/codeview-dev/user-data/trace/${dirname}/trace.hrf /tmp"
  $cmd
}

# Functions made as bash cmd.
grepkill () {
  ps auxwww| grep $1 | grep -v grep | awk "{print \$2}" | xargs kill -9
}

g4evi() {
  g4 edit $1
  vi $1
}

alias s='sudo su -'
S() {
  grep -r -A $1 -B $1 $2 $3
}

sinf() {
  find . -name $1 | xargs grep $2
}

sf() {
  find . | grep $1
}


search() {
  find . -name ${2:-"*"} | xargs grep $1
}

portfwd() {
  # $1 port number $2 user $3 host
  ssh -L $1:localhost:$1 $2@$3
}

grepbsf() {
  grep -A $1 $2 ~/.bashrc
}

cutlines() {
  arg1=$1
  arg2=$2
  arg3=$3
  echo "${arg1} $arg2 arg3" $arg1 ${arg2}
  awk -FS'\n' "{if (NR%$1 == $2) print }" $3
}
# Joining lines
# awk  'BEGIN{ORS="\t"}{if (FNR%3 ==0) {print $0"\n"} else {print $0}}' /tmp/b
joinlines() {
  arg1=$1
  arg2=$2
  arg3=$3
  awk  -v arg1=$arg1 -v arg2=$arg2 'BEGIN{ORS="\t"}{if (FNR%arg1 == arg2) {print $0"\n"} else {print $0}}' $arg3
}

mydiff() {
  vdiff $1/$2 $2
}

removelogprefix() {
  awk "{ if (\$0 ~ /^I$1/) {print substr(\$0, 29)} else {print \$0}}" $2 > $2.c
}

cpto() {
  cp $2 $1
}

cpallto() {
  arg1=$1
  shift
  for f in  $* ; do
    echo "copying " $f " to " $arg1
    cp $f $arg1 
  done;
}

buildwithlockfile() {
  touch ~/tmp/mylockfile
  #blaze build --use_sponge=no $*
  mvn install
  if [ $? -ne 0 ]; then
    echo "build broke"
    return 225
  fi
  yes | rm -f ~/tmp/mylockfile
}

runAfterLock() {
  while [ -e ~/tmp/mylockfile ]
  do
    sleep 1
    echo "waiting for the other cmd to release lock"
  done
  $*
}

runInLoop() {
  while [ 1 ]
  do
    $*
    sleep 30
  done
}

untarit(){
  tar xvfz $1
}
tarit(){
  filename=`basename $1`
  tar cvfz ${filename}.tar.gz $1 
}
function br_sum()
{
awk '{ sum += $1 } END { print sum }'
}

function uhost()
{
al=$1
echo `alias $al | awk '{print $3}' | cut -f 1 -d \'`
}


function listtabsep() {
  local file=$1
  local cmd=${2:-"head"}
  ${cmd} -1 ${file} | awk -F'\t' '{for (i=1;i<=NF;i++) print i " : " $i}'
}


function list_column_index() {
  file_name=${1:?File missing}
  column=${2:?Column missing}
  separator=${3:-'\t'}
  alias tmpcmd="""awk -F'${separator}' '{print \$${column}}' ${file_name}"""
  tmpcmd
}


function list_header_index() {
  file_name=${1:?File missing}
  local separator=${2:-""}
  if [[ ${separator} == "" ]]; then
    separator='\t'
  fi
  (head -1 ${file_name} | awk -F${separator} '{for (i=1; i<=NF; i++) {print i " : " $i}}')
}
alias tomcat='sudo /etc/init.d/tomcat start'
alias stoptomcat='sudo /etc/init.d/tomcat stop'

alias opengrok='cd /home/arulsmv/project/OpenGrok'
alias groksrc='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok'
alias grokweb='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok/web'
alias grokutil='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok/util'
alias groksearch='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok/search'
alias groka='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok/analysis'
alias grokconf='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok/configuration'
alias grokindex='cd /home/arulsmv/project/OpenGrok/src/org/opensolaris/opengrok/index'
build_deploy(){
  #cd /home/arulsmv/project/OpenGrok
  #mvn -DskipTests=true package
  ant && sudo OPENGROK_TOMCAT_BASE=/opt/tomcat ./OpenGrok deploy && stoptomcat  && tomcat
  #cd -
}
build_index() {
  sudo /home/arulsmv/project/OpenGrok/OpenGrok index $1 
  echo "Indexed..."
  sudo vi /var/opengrok/etc/configuration.xml
  stoptomcat && tomcat
}

