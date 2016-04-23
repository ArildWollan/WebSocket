package ws;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class WebSocketMessage {
	private boolean fin;
	private int opcode;
	private int payloadLength;
	private byte[] payload;

	public WebSocketMessage(InputStream inputStream) throws IOException {

		// Fin + OpCode byte
		byte[] b = new byte[2];
		inputStream.read(b);
		fin = ((b[0] & 0x80) != 0);
		opcode = (byte) (b[0] & 0x0F);

		// Masked + Payload Length
		boolean masked = ((b[1] & 0x80) != 0);
		long payloadLength = (byte) (0x7F & b[1]);
		int byteCount = 0;
		System.out.println("Payload length 1: " + payloadLength);
		if (payloadLength == 0x7F) {
			// 8 byte extended payload length
			byteCount = 8;
			byte[] payloadLengthByteArray = new byte[byteCount];
			inputStream.read(payloadLengthByteArray);
			ByteBuffer wrapped = ByteBuffer.wrap(payloadLengthByteArray);
			payloadLength = wrapped.getLong(); // Long because it's 8 bytes.
		} else if (payloadLength == 0x7E) {
			// 2 bytes extended payload length
			byteCount = 2;
			byte[] payloadLengthByteArray = new byte[byteCount];
			inputStream.read(payloadLengthByteArray);
			ByteBuffer wrapped = ByteBuffer.wrap(payloadLengthByteArray);
			payloadLength = wrapped.getShort(); // short because it's only two
												// bytes
		}

		byte maskingKey[] = null;
		if (masked) {
			// Masking Key
			maskingKey = new byte[4];
			inputStream.read(maskingKey);
		}

		// Payload itself, casting to int TODO: What if payload is bigget than
		// an int?
		payload = new byte[(int) payloadLength];
		inputStream.read(payload);
		if (masked) {
			for (int i = 0; i < payload.length; i++) {
				payload[i] ^= maskingKey[i % 4];
			}
		}

	}

	public byte[] getFrame() {
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

	public boolean isDisconnect() {
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

	public String getPayloadAsString() {
		return new String(payload, Charset.forName("UTF-8"));
	}

}
