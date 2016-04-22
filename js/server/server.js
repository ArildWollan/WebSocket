'use strict';

var express = require('express');

//See https://github.com/websockets/ws
var ws = require('ws');

var http_server = express();

//This resource makes it possible to download and start the WebSocket client
http_server.use(express.static(__dirname + "/../client"));

//Start the web server serving the WebSocket client
//Open http://localhost:3000 in a web browser
var server=http_server.listen(3000, () => {
  var host = server.address().address;
  var port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});
