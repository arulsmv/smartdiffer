#!/usr/bin/env python

import commands 
import os
import random
import sys

import argparse
import difflib
import git
import json

from py2neo import Graph, Node, Relationship, Path
from python import function_extractor

def get_details(repo, cmd_param , combined_functions):
  repo.git.checkout(cmd_param['commit_id'])
  cmd = "%(opengrok_sh)s index %(directory)s/" % cmd_param
  status, output = commands.getstatusoutput(cmd)
  cmd = "java -jar %(scoper_jar)s -R %(environment)s -i %(tmp_dir)s/%(prefix)d_%(run)d.txt -o %(tmp_dir)s/%(prefix)d_%(run)d.out" % cmd_param
  status, output = commands.getstatusoutput(cmd)
  with open ("%(tmp_dir)s/%(prefix)d_%(run)d.out" % cmd_param) as f:
    call_graph = json.load(f)
    f.close()

  for entry in call_graph:
    key = entry['referringFunction'] + "@" + entry['referringFile']
    if not combined_functions.get(key):
      combined_functions[key] = {}
    method_known = combined_functions.get(key).get(cmd_param['run'], None)
    if not method_known:
      combined_functions.get(key)[cmd_param['run']] = function_extractor.get_method_content(cmd_param['directory'], entry['referringFile'],entry['referringFunction'], entry['referringLine'])

    key = entry['referredFunction'] + "@" + entry['referredFile']
    if not combined_functions.get(key):
      combined_functions[key] = {}
    method_known = combined_functions.get(key).get(cmd_param['run'], None)
    if not method_known:
      combined_functions.get(key)[cmd_param['run']] = function_extractor.get_method_content(cmd_param['directory'], entry['referredFile'],entry['referredFunction'], entry['referredLine'])
  return call_graph


def build_graph(delta, call_graph1, call_graph2, output_file):
  nodes = [] 
  relation = []
  index_map = {}
  index = 0
  for key in delta.keys():
    nodes.append({"id": key, "label": delta[key], "shape": "box", "font": {"face":"monospace", "align":"left"}})
    index_map[key] = index
    index += 1

  for entry in call_graph1:
    calling_fun = entry['referringFunction'] + "@" + entry['referringFile']
    called_fun = entry['referredFunction'] + "@" + entry['referredFile']
    relation.append({"from": calling_fun, "to": called_fun, "arrows": "to", "physics": "false", "smooth": {"type": "cubicBezier"}, "label":"Original"}) 
    #relation.append((calling_fun, "Original Call To", called_fun))
    
  for entry in call_graph2:
    calling_fun = entry['referringFunction'] + "@" + entry['referringFile']
    called_fun = entry['referredFunction'] + "@" + entry['referredFile']
    relation.append({"from": calling_fun, "to": called_fun, "arrows": "to", "physics": "false", "smooth": {"type": "cubicBezier"}, "label":"Modified"}) 
    #relation.append(( calling_fun, "Modified Call To", called_fun))

  fp = open(output_file + ".nodes", "w")
  json.dump(nodes,  fp)
  fp.close()
  fp = open(output_file + ".edges", "w")
  json.dump(relation, fp)
  fp.close()

  
 
def build_neo_graph(delta, call_graph1, call_graph2):
  nodes = {}
  graph = Graph(password='smd')
  tx = graph.begin()
  for key in delta.keys():
    nodes[key] = Node("function", title=key, content=delta[key])
    tx.merge(nodes[key])

  for entry in call_graph1:
    calling_fun = entry['referringFunction'] + "@" + entry['referringFile']
    called_fun = entry['referredFunction'] + "@" + entry['referredFile']
    relation = Relationship(nodes[calling_fun], "Original Call To", nodes[called_fun])
    tx.merge(relation)

    
  for entry in call_graph2:
    calling_fun = entry['referringFunction'] + "@" + entry['referringFile']
    called_fun = entry['referredFunction'] + "@" + entry['referredFile']
    relation = Relationship(nodes[calling_fun], "Modified Call To", nodes[called_fun])
    tx.merge(relation)
 
  tx.commit()

    
  


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

  cmd_param['run'] = 1
  cmd_param['commit_id'] = args.commit_from
  combined_function = {}
  call_graph1 = get_details(repo, cmd_param, combined_function)

  cmd_param['run'] = 2
  cmd_param['commit_id'] = args.commit_to
  call_graph2 = get_details(repo, cmd_param, combined_function)
  delta_contents = {}
  for key in combined_function.keys():
    original = combined_function.get(key).get(1, [])
    modified = combined_function.get(key).get(2, [])
    delta_contents[key] = "".join(difflib.ndiff(original, modified))
  # free up unused data structures
  del combined_function
  del repo

  build_graph(delta_contents, call_graph1, call_graph2, "%(tmp_dir)s/%(prefix)d" % cmd_param)
  

if __name__ == "__main__":
  main()
