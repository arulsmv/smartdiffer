#!/usr/bin/env python

import commands 
import os
import random
import sys

import argparse
import git

def main():
  parser = argparse.ArgumentParser("Builds the diff graph between two committs")
  parser.add_argument("--src_directory", default="./")
  parser.add_argument("--commit_from", default="HEAD~1")
  parser.add_argument("--commit_to", default="HEAD")
  parser.add_argument("--tmp_dir", default="/tmp")
  args = parser.parse_args()
  
  repo  = git.Repo(args.src_directory)
  sd_tools_dir = os.getenv("SD_TOOLS_DIR", "/usr/lib/share/sd")
  prefix = int(random.random() * 100000)
  # Rewrite the java program GitDiffer in python; we cannot change directory in java
  cmd_param = {
                "directory" : args.src_directory,
                "differ_jar" : os.path.join(sd_tools_dir, "differ/target/I-1.0-SNAPSHOT-jar-with-dependencies.jar"),
                "opengrok_sh" : os.path.join(sd_tools_dir, "OpenGrok/OpenGrok"),
                "scoper_jar" : os.path.join(sd_tools_dir, "graph-builder/target/smartdiffer-app-1.0-SNAPSHOT-jar-with-dependencies.jar"),
                "from" : args.commit_from,
                "to" : args.commit_to,
                "tmp_dir" : args.tmp_dir,
                "environment" : os.environ.get("OPENGROK_CONFIG", "/var/opengrok/etc/configuration.xml"),
                "prefix": prefix
              }
  cmd = "cd %(directory)s; java -jar %(differ_jar)s -f %(from)s -t %(to)s -o1 %(tmp_dir)s/%(prefix)d_1.txt -o2 %(tmp_dir)s/%(prefix)d_2.txt " % cmd_param
  status, output = commands.getstatusoutput(cmd)
  repo.git.checkout(args.commit_from)
  cmd = "%(opengrok_sh)s index %(directory)s/" % cmd_param
  status, output = commands.getstatusoutput(cmd)
  cmd = "java -jar %(scoper_jar)s -R %(environment)s -i %(tmp_dir)s/%(prefix)d_1.txt -o %(tmp_dir)s/%(prefix)d_1.out" % cmd_param
  status, output = commands.getstatusoutput(cmd)

  repo.git.checkout(args.commit_to)
  cmd = "%(opengrok_sh)s index %(directory)s/" % cmd_param
  status, output = commands.getstatusoutput(cmd)
  cmd = "java -jar %(scoper_jar)s -R %(environment)s -i %(tmp_dir)s/%(prefix)d_2.txt -o %(tmp_dir)s/%(prefix)d_2.out" % cmd_param
  status, output = commands.getstatusoutput(cmd)

  

if __name__ == "__main__":
  main()
