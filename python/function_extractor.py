import os

def read_file(repo_src, file_path):
  if file_path.startswith("/"):
    file_path=file_path[1:]
  with open(os.path.join(repo_src, file_path)) as file_p:
    contents = file_p.readlines()
    return contents

def file_type(file_path):
  return file_path.split('.')[-1].trim()

def extract_function_java(content, linenumber, method_name):
  while(content[linenumber].rfind(method_name) == -1):
    linenumber -= 1
  line_start =linenumber
  #find first {
  while(content[linenumber].find('{')) == -1:
   linenumber += 1
  opened_curly_parenthesis = 0
  for char in content[linenumber]:
    if char == '{' : opened_curly_parenthesis +=1
    elif char == '}' : opened_curly_parenthesis -=1
  while (opened_curly_parenthesis > 0 and linenumber < len(content)):
    linenumber += 1
    for char in content[linenumber]:
      if char == '{' : opened_curly_parenthesis +=1
      elif char == '}' : opened_curly_parenthesis -=1
  return line_start, linenumber


def extract_function_python(content, linenumber, method_name):
  while(content[linenumber].rfind(method_name) == -1):
    linenumber -= 1
  line_start =linenumber
  print content[linenumber]
  m=content[linenumber].find("def ")
  print m
  prefix=content[linenumber][0:m]
  linenumber += 1
  while (linenumber < len(content) and ( content[linenumber][0] =='\n' or len ( content[linenumber]) > m and content[linenumber][0:m] == prefix and content[linenumber][m+1] in [' ', '\t'])):
    linenumber+=1
  return line_start, linenumber
