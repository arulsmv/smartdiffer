#!/usr/bin/python
import argparse
import sys

def main():
  parser = argparse.ArgumentParser("Builds the diff graph between two committs")
  parser.add_argument("--src_directory", default="./")
  parser.add_argument("--from", default="HEAD~1")
  parser.add_argument("--to", default="HEAD")
  parser.parse_args()
  
  

if __name__ == "__main__":
  main()
