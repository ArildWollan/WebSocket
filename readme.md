**WebSocket-Project as part of  TDAT2004-A**

*NTNU IIE, Spring 2016*

*Participants: Henrik Width and Arild Wollan*

The assignment is solved by presenting the user with a chat server.

The program is threaded and upon running it will start two threads, a simple webserver on port 8080 and a websocket server on port 3002.

1. Compile:
```sh
javac -cp src/ src/**/*.java
```

2. Run:
```sh
java -cp ./src/ ws.WebSocketServer
```

3. Start the servers
![Console started](https://github.com/ArildWollan/WebSocket/blob/master/src/console.png)



4. Open http://localhost:8080
