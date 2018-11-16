var http = require('http')
    fs = require('fs')
    //d3 = require('d3')
    //vis = require('vis')
    url = require("url")

var data_dir = process.argv[2]

http.createServer(function (req, res) {
  if(req.url.indexOf('favicon.ico') != -1) {
    res.statusCode = 404
    return
  }
  var path = url.parse(req.url).pathname;
  console.log(path);
  if (path.match("/vis/vis*")) {
    res.write(fs.readFileSync(__dirname + path));
    res.end();
  } else if (path.match("/c*")) { 
    res.write(fs.readFileSync(__dirname + "/callgraph-header.html"))
    res.write("var nodes =" + fs.readFileSync(data_dir + path + "/_.nodes") + ";\n")
    res.write("var edges =" + fs.readFileSync(data_dir + path + "/_.edges") + ";\n")
    res.write(fs.readFileSync(__dirname + "/callgraph-footer.html"))
    res.end()
  } else {
    console.log("error in " + path);
  }
}).listen(1337, '127.0.0.1')

console.log('Server running at http://127.0.0.1:1337/');
