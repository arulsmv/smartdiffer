#!/usr/bin/env python

import commands 
import os
import random
import sys

import argparse
import git
import json

from python import function_extractor

def get_details(repo, cmd_param , combined_functions):
  repo.git.checkout(cmd_param['commit_id'])
  cmd = "%(opengrok_sh)s index %(directory)s/" % cmd_param
  status, output = commands.getstatusoutput(cmd)
  cmd = "java -jar %(scoper_jar)s -R %(environment)s -i %(tmp_dir)s/%(prefix)d_%(run)d.txt -o %(tmp_dir)s/%(prefix)d_%(run)d.out" % cmd_param
  status, output = commands.getstatusoutput(cmd)
  with open ("%(tmp_dir)s/%(prefix)d_%(run)d.out" % cmd_param) as f:
    call_graph = json.load(f)

  for entry in call_graph:
    key = entry['referringFunction'] + "@" + entry['referringFile']
    if not combined_functions.get(key):
      combined_functions[key] = {}
    method_known = combined_functions.get(key).get(cmd_param['run'], None)
    if not method_known:
      print "referring", entry['referringFile'], "@", entry['referringFunction'],":", entry['referringLine']
      combined_functions.get(key)[cmd_param['run']] = function_extractor.get_method_content(cmd_param['directory'], entry['referringFile'],entry['referringFunction'], entry['referringLine'])

    key = entry['referredFunction'] + "@" + entry['referredFile']
    if not combined_functions.get(key):
      combined_functions[key] = {}
    method_known = combined_functions.get(key).get(cmd_param['run'], None)
    if not method_known:
      print "referring", entry['referringFile'], "@", entry['referringFunction'],":", entry['referringLine']
      combined_functions.get(key)[cmd_param['run']] = function_extractor.get_method_content(cmd_param['directory'], entry['referredFile'],entry['referredFunction'], entry['referredLine'])


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
  """ 
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
  with open ("%(tmp_dir)s/%(prefix)d_1.out" % cmd_param) as f:
    call_graph1 = json.load(f)
  with open ("%(tmp_dir)s/%(prefix)d_2.out" % cmd_param) as f:
    call_graph2 = json.load(f)

  combined_functions = {} 
  repo.git.checkout(args.commit_from)
  for entry in call_graph1:
    key = entry['referringFunction'] + "@" + entry['referringFile']
  """
  cmd_param['run'] = 1
  cmd_param['commit_id'] = args.commit_from
  combined_function = {}
  get_details(repo, cmd_param, combined_function)

  cmd_param['run'] = 2
  cmd_param['commit_id'] = args.commit_to
  get_details(repo, cmd_param, combined_function)
   
  print combined_function



  

if __name__ == "__main__":
  main()
