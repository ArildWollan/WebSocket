**WebSocket-Project as part of  TDAT2004-A**

*NTNU IIE, Spring 2016*

*Participants: Henrik Width and Arild Wollan*

The assignment is solved by presenting the user with a chat client which connects to a WebSocket server implemented in java.

The program is threaded and upon running it will start two threads, a simple webserver on port 8080 and a websocket server on port 3002.

# Installation

A runnable jar file is included for convenience, this is runnable from a terminal. 
```sh
java -jar WebSocket.jar
```

If you want to compile from scratch, follow these steps.

1. Clone the repository.
```sh
git clone https://github.com/ArildWollan/WebSocket.git
```

- Compile.
```sh
cd WebSocket
javac -cp src/ src/**/**/*.java
```

- Run (from WebSocket directory).
```sh
java -cp ./src/:./txt/ nettverksprosjekt.servers.WebSocketServer
```

- Start the servers
![Console started](https://raw.githubusercontent.com/ArildWollan/WebSocket/master/doc/console.png?token=AEeDt0uSeiStyOk9pkAhqIwkwpayFh50ks5XKJGVwA%3D%3D)

  - start, starts both servers, type help to display list of commands.

- Open http://localhost:8080

# Documentation

This project has no dependencies and is tested on Oracle Java 8 running on OSX 10.11.4 and Ubuntu 16.04. Browsers include Chrome 50.0.226, Vivaldi 1.1.453.47, and Firefox 46.0. See javadoc in each class for more information.

## Webserver
The webserver only exists to serve a single page with a javascript-solution for chat. This can be shut down if you have a different way of connecting to the WebSocket Server.

## Websocket Server
When using the standard settings, The server accepts up to 100 connections on port 3002. both the port number and connection count can be customized. The main functionality is to separate each connection to its own thread and facitiltate broadcasting messages. The server supports text frames smaller than 2^31 - 8 bytes (see References).

# Further work
If we were to continue working on this project, the following areas would be of interest:

- Extracting server functionality into own interface.
- Ping/Pong functionality.
- Closing connection from server side.
- Refactor with respect to inheritance.
- Accepting binary frames.
- Handle Continuitation frames.
- Handle very large payloads.

# References

- http://stackoverflow.com/questions/3038392/do-java-arrays-have-a-maximum-size/8381338#8381338
- http://stackoverflow.com/questions/18368130/how-to-parse-and-validate-a-websocket-frame-in-java
- https://tools.ietf.org/html/rfc6455
- http://stackoverflow.com/questions/14790440/java-get-my-own-ip-address-in-my-home-network
