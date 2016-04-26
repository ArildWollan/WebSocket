WebSocket-Project as part of  TDAT2004-A

NTNU IIE, Spring 2016

Participants: Henrik Width and Arild Wolland

The assignment is solved by presenting the user with a chat server.

The program is threaded and upon running it will start two threads, a simple webserver on port 8080 and a websocket server on port 3002.

Compile:
```sh
javac -cp src/ src/**/*.java
```

Run:
```sh
java -cp ./src/ ws.WebSocketServer
```
