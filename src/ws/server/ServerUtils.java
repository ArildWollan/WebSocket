package ws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class ServerUtils {

	public static byte[] getPayloadFromFrame(byte[] frame) {
		int maskIndex = 2;

		// If we get opcode 8, then return null because it is a disconnect.
		if((frame[0] & 0x0f) == 8) {
			return null;
		}
		
		byte[] maskBytes = new byte[4];

		//
		if ((frame[1] & 0x7f) == 126) {
			maskIndex = 4;
		} else if ((frame[1] & 0x7f) == 127) {
			maskIndex = 10;
		}

		System.arraycopy(frame, maskIndex, maskBytes, 0, 4);

		byte[] message = new byte[frame.length - maskIndex - 4];
		// Section 5.3 of RFC6455
		for (int i = maskIndex + 4; i < frame.length; i++) {
			message[i - maskIndex - 4] = (byte) (frame[i] ^ maskBytes[(i - maskIndex - 4) % 4]);
		}

		// _rawIn[1] & 0x7f is the payload length
		return Arrays.copyOfRange(message, 0, frame[1] & 0x7f);
	}

	public static byte[] getFrameFromPayload(String message) {

		byte[] rawData = message.getBytes();

		int maskIndex = 0;
		byte[] frame = new byte[10];

		// Set first byte values
		frame[0] = (byte) 129; // Final, text frame (10000001)

		if (rawData.length <= 125) {
			frame[1] = (byte) rawData.length;
			maskIndex = 2;
		} else if (rawData.length >= 126 && rawData.length <= 65535) {
			frame[1] = (byte) 126;
			int len = rawData.length;
			frame[2] = (byte) ((len >> 8) & 0xff);
			frame[3] = (byte) (len & 0xff);
			maskIndex = 4;
		} else {
			frame[1] = (byte) 127;
			int len = rawData.length;
			frame[2] = (byte) ((len >> 56) & 0xff);
			frame[3] = (byte) ((len >> 48) & 0xff);
			frame[4] = (byte) ((len >> 40) & 0xff);
			frame[5] = (byte) ((len >> 32) & 0xff);
			frame[6] = (byte) ((len >> 24) & 0xff);
			frame[7] = (byte) ((len >> 16) & 0xff);
			frame[8] = (byte) ((len >> 8) & 0xff);
			frame[9] = (byte) (len & 0xff);
			maskIndex = 10;
		}

		int bLength = maskIndex + rawData.length;

		byte[] reply = new byte[bLength];

		int bLim = 0;
		for (int i = 0; i < maskIndex; i++) {
			reply[bLim] = frame[i];
			bLim++;
		}
		for (int i = 0; i < rawData.length; i++) {
			reply[bLim] = rawData[i];
			bLim++;
		}

		return reply;
	}
	

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

	public static void readClientHeaders(BufferedReader br, ArrayList<String> clientheaders) throws IOException {
		String line = br.readLine();
		clientheaders.add(line);			
		while(line.trim().length() != 0) {
			line = br.readLine();
			clientheaders.add(line);
		}
		
	}

}
