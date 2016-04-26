**WebSocket-Project as part of  TDAT2004-A**

*NTNU IIE, Spring 2016*

*Participants: Henrik Width and Arild Wollan*

The assignment is solved by presenting the user with a chat server.

The program is threaded and upon running it will start two threads, a simple webserver on port 8080 and a websocket server on port 3002.

# Installation
1. Clone the repository (optional)
```sh
git clone https://github.com/ArildWollan/WebSocket.git
```

- Compile:
```sh
cd WebSocket
javac -cp src/ src/**/*.java
```

- Run (from WebSocket direcotry):
```sh
java -cp ./src/ ws.WebSocketServer
```

- Start the servers
![Console started](https://raw.githubusercontent.com/ArildWollan/WebSocket/master/doc/console.png?token=AEeDt0uSeiStyOk9pkAhqIwkwpayFh50ks5XKJGVwA%3D%3D)

  - auto starts both servers, type help to display list of commands

- Open http://localhost:8080

# Documentation

This project has no dependencies and is tested on java 8
