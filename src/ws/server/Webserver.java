package ws.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import ws.utils.FileHandler;

public class Webserver implements Runnable{
	
	private void sendWebsite(PrintWriter pw) {
		ArrayList<String> website = FileHandler.readFile("txt/index.txt");
		for(String s : website) {
			System.out.println("reading");
			pw.println(s);
		}
		pw.flush();
	}

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
                sendWebsite(pw);
            }
        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
