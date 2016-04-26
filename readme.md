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

  - start, starts both servers, type help to display list of commands.

- Open http://localhost:8080

# Documentation

This project has no dependencies and is tested on Oracle Java 8 running on OSX 10.11.3 and Ubuntu 16.04. See javadoc in each class for more information.

## Webserver
The webserver only exists to serve a single page with a javascript-solution for chat. This can be shut down if you have a different way of connecting to the WebSocket Server.

## Websocket Server
When using the standard settings, The server accepts up to 100 connections on port 3002. both the port number and connection count can be customized. The main functionality is to separate each connection to its own thread and facitiltate broadcasting messages.

# Further work
If we were to continue working on this project, the following areas would be of interest:

1. Extracting server functionality into own interface.
2. Ping/Pong functionality.
3. Closing connection from server side.
4. ...


# References

- http://stackoverflow.com/questions/18368130/how-to-parse-and-validate-a-websocket-frame-in-java

- https://tools.ietf.org/html/rfc6455

- http://stackoverflow.com/questions/14790440/java-get-my-own-ip-address-in-my-home-network
