package ws.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class ServerUtils {	

	public static String getSecWebsocketAccept(String key){
		String guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		String acceptkey = key + guid;
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			return Base64.getEncoder().encodeToString(digest.digest(acceptkey.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "SHA-1 NOT FOUND";
	}
		
	public static String parseAndGetWebsocketAccept(ArrayList<String> clientheaders){
		for(String header : clientheaders) {
			if (header.split(":")[0].trim().equals("Sec-WebSocket-Key")) {
				return getSecWebsocketAccept(header.split(":")[1].trim());				
			}
		}
		return "Sec-WebSocket-Key: NOT FOUND";
		
	}
	public static String parseAndGetWebsocketAccept(Map<String, String> clientheaders) {
		return getSecWebsocketAccept(clientheaders.get("Sec-WebSocket-Key"));
	}
}
