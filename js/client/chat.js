
var ws =  new WebSocket("ws://localhost:3002");
var name;

$(document).ready(function(){
  getName();
  console.log(ws)

  ws.onerror = function(msg) {
    console.log(msg);
    $("#connection_label").html("Not connected");
  };

  ws.onopen = () => {
    $("#connection_label").html("Connected");
  };

  ws.onmessage = (event) => {
    var json = JSON.parse(event.data);

    if(json.message){
      var chatstring = json.message.time + "\t" + json.message.name + "\t" + json.message.msg + "\n";
      var newtext = document.getElementById("chatarea").value + chatstring;
      console.log(newtext);

      $("#chatarea").html(newtext);
    }

  };

  ws.onclose = function(message) {
    $("#connection_label").html("Not connected");
  };

  window.onkeyup = function(e) {
   var key = e.keyCode ? e.keyCode : e.which;
   if (key == 13) {
     handleClick();
   }
  }
});

function getName(){
	name = prompt("Enter name","Anonymous");
}

function handleClick(){
  var message = document.getElementById('chatbox').value;

  var datestamp = new Date();

  if(ws.readyState == 1) {
    var json = {"message": {"time" : datestamp, "name": name, "msg": message }};
    this.ws.send(JSON.stringify(json));
  }

  document.getElementById('chatbox').value = "";
}