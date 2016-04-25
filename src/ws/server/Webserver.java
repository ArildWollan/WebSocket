package ws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserver implements Runnable{

	@Override
	public void run() {
		try{
		int port = 8080;
        System.out.println("Web server started - Ready to accept connections on port " + port);
        ServerSocket server = new ServerSocket(port);

        while (true) {
            try (Socket connection = server.accept()) {
                System.out.println(connection.getInetAddress().getHostAddress() + " has connected");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                PrintWriter pw = new PrintWriter(connection.getOutputStream());

                // Send server headers
                pw.println("HTTP/1.0 200 OK");
                pw.println("Content-Type: text/html");
                pw.println(""); // End of headers
                // Send the HTML page

                pw.println("<!DOCTYPE HTML>                                                                                            ");
                pw.println("<html>                                                                                                     ");
                pw.println("  <head>                                                                                                   ");
                pw.println("	<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js'></script>              ");
                pw.println("    <link href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css' rel='stylesheet'>   ");
                pw.println("    <script>                                                                        ");
                pw.println("                                                                                                           ");
                pw.println("	  var ws =  new WebSocket('ws://localhost:3002');                                                      ");
                pw.println("	  var name;                                                                                            ");
                pw.println("                                                                                                           ");
                pw.println("	  $(document).ready(function(){                                                                        ");
                pw.println("                                                                                                           ");
                pw.println("	    getName();                                                                                         ");
                pw.println("	    $('#name_label').html('Name: ' + name);                                                            ");
                pw.println("	    console.log(ws)                                                                                    ");
                pw.println("                                                                                                           ");
                pw.println("	    ws.onerror = function(msg) {                                                                       ");
                pw.println("	      console.log(msg);                                                                                ");
                pw.println("	      $('#connection_label').html('Not connected');                                                    ");
                pw.println("	    };                                                                                                 ");
                pw.println("                                                                                                           ");
                pw.println("	    ws.onopen = () => {                                                                                ");
                pw.println("	      $('#connection_label').html('Connected');                                                        ");
                pw.println("	    };                                                                                                 ");
                pw.println("                                                                                                           ");
                pw.println("	    ws.onmessage = (event) => {                                                                        ");
                pw.println("	      var json = JSON.parse(event.data);                                                               ");
                pw.println("                                                                                                           ");
                pw.println("	      if(json.message){                                                                                ");
                pw.println("	        var chatstring = json.message.time + '\t' + json.message.name + '\t' + json.message.msg + '\\n';");
                pw.println("	        var newtext = document.getElementById('chatarea').value + chatstring;                          ");
                pw.println("	        console.log(newtext);                                                                          ");
                pw.println("                                                                                                           ");
                pw.println("	        $('#chatarea').html(newtext);                                                                  ");
                pw.println("	      }                                                                                                ");
                pw.println("                                                                                                           ");
                pw.println("	    };                                                                                                 ");
                pw.println("                                                                                                           ");
                pw.println("	    ws.onclose = function(message) {                                                                   ");
                pw.println("	      $('#connection_label').html('Not connected');                                                    ");
                pw.println("	    };                                                                                                 ");
                pw.println("                                                                                                           ");
                pw.println("	    window.onkeyup = function(e) {                                                                     ");
                pw.println("	     var key = e.keyCode ? e.keyCode : e.which;                                                        ");
                pw.println("	     if (key == 13) {                                                                                  ");
                pw.println("	       handleClick();                                                                                  ");
                pw.println("	     }                                                                                                 ");
                pw.println("	    }                                                                                                  ");
                pw.println("	  });                                                                                                  ");
                pw.println("                                                                                                           ");
                pw.println("                                                                                                           ");
                pw.println("	  function handleClick(){                                                                              ");
                pw.println("	    var message = document.getElementById('chatbox').value;                                            ");
                pw.println("                                                                                                           ");
                pw.println("	    var datestamp = new Date();                                                                        ");
                pw.println("                                                                                                           ");
                pw.println("	    if(ws.readyState == 1) {                                                                           ");
                pw.println("	      var json = {'message': {'time' : datestamp, 'name': name, 'msg': message }};                     ");
                pw.println("	      this.ws.send(JSON.stringify(json));                                                              ");
                pw.println("	    }                                                                                                  ");
                pw.println("                                                                                                           ");
                pw.println("	    document.getElementById('chatbox').value = '';                                                     ");
                pw.println("	  }                                                                                                    ");
                pw.println("                                                                                                           ");
                pw.println("	  function getName(){                                                                                  ");
                pw.println("	    name = prompt('Please enter your name', 'Anonymous');                                              ");
                pw.println("	  }                                                                                                    ");
                pw.println("	                                                                                                       ");
                pw.println("	</script>                                                                                              ");
                pw.println("  </head>                                                                                                  ");
                pw.println("  <body>                                                                                                   ");
                pw.println("    <div id='connection_label'>                                                                            ");
                pw.println("      Connecting...                                                                                        ");
                pw.println("    </div>                                                                                                 ");
                pw.println("                                                                                                           ");
                pw.println("    <div id='name_label'>                                                                                  ");
                pw.println("      Name: ...                                                                                            ");
                pw.println("    </div>                                                                                                 ");
                pw.println("    <textarea style='width: 80%' id='chatarea' columns='20' rows='10'></textarea>                          ");
                pw.println("    <br>                                                                                                   ");
                pw.println("      <label>Chat:</label>                                                                                 ");
                pw.println("      <input type='text' id='chatbox' required>                                                            ");
                pw.println("      <button type='submit' onClick='handleClick()' class='btn btn-success btn-sm'>Send</button>           ");
                pw.println("  </body>                                                                                                  ");
                pw.println("</html>                                                                                                    ");
                pw.flush();
            }
        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
