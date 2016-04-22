package ws;

import java.nio.charset.Charset;
import java.util.Arrays;

public class WebSocketMessage {

	private boolean fin;
	private int opcode;
	private int payloadLength;
	private byte[] payload;
	
	public WebSocketMessage(byte[] maskeddata){
		
		// Check if final fragment
		// 0x80 masks the first bit
		if ((maskeddata[0] & 0x80) == 1) {
			fin = true;
		}
		
		// get opcode, 
		// 0x0F masks the last four bit
		opcode = (maskeddata[0] & 0x0F);
		
		//get the paload length
		// 0x7F masks the last 7 bit
		// TODO: fix if payload.size is 126 or 127, which means 3 or 9 bytes
		payloadLength = (maskeddata[1] & 0x7F);
		
		int maskIndex = 2;
		
		byte[] maskBytes = new byte[4];

		// move start index if payload lenght is 126 or 127.
		if ((maskeddata[1] & 0x7f) == 126) {
			maskIndex = 4;
		} else if ((maskeddata[1] & 0x7f) == 127) {
			maskIndex = 10;
		}

		System.arraycopy(maskeddata, maskIndex, maskBytes, 0, 4);

		byte[] message = new byte[maskeddata.length - maskIndex - 4];
		// Section 5.3 of RFC6455
		for (int i = maskIndex + 4; i < maskeddata.length; i++) {
			message[i - maskIndex - 4] = (byte) (maskeddata[i] ^ maskBytes[(i - maskIndex - 4) % 4]);
		}

		payload =  Arrays.copyOfRange(message, 0, payloadLength);
		
	}
		
	public byte[] getFrame(){
		byte[] rawData = payload;

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

	public boolean isFin() {
		return fin;
	}

	public boolean isDisconnect(){
		return opcode == 8;
	}
	
	public int getOpcode() {
		return opcode;
	}

	public int getPayloadLength() {
		return payloadLength;
	}

	public byte[] getPayload() {
		return payload;
	}
	
	public String getPayloadAsString(){
		return new String(payload, Charset.forName("UTF-8"));
	}
	
}
