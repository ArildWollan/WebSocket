package ws;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
/**
 * WebSocketMessage reads bytes from an inputstream and decodes the frame.
 * Please note that if payloadlength is decoded as a Long number greater than MAX_INT,
 * initializing the payload byte array will fail.
 * @author henrik
 *
 */
public class WebSocketMessage {
	/** FIN flag, first bit of first byte **/
	private boolean fin;
	/** opcode number, last nibble of first byte
	 * see RFC6455 section 5.2 for details.
	 */
	private int opcode;
	/**at least last 7 bits of second byte
	 * but could be more.
	 * see RFC6455 section 5.2 for details.
	 * **/
	private int payloadLength;
	/**
	 * Demasked payload, see note about payloadlength > MAX_INT.
	 */
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
		
		// Payloadlength might be larger than 7 bits if == 126 or 127.
		// We´ll get the short or long depending on the value.
		if (payloadLength == 0x7F) {
			// 8 byte extended payload length.
			byteCount = 8;
			byte[] payloadLengthByteArray = new byte[byteCount];
			inputStream.read(payloadLengthByteArray);
			ByteBuffer wrapped = ByteBuffer.wrap(payloadLengthByteArray);
			payloadLength = wrapped.getLong(); // Long because it's 8 bytes.
		} else if (payloadLength == 0x7E) {
			// 2 bytes extended payload length.
			byteCount = 2;
			byte[] payloadLengthByteArray = new byte[byteCount];
			inputStream.read(payloadLengthByteArray);
			ByteBuffer wrapped = ByteBuffer.wrap(payloadLengthByteArray);
			payloadLength = wrapped.getShort(); // short because it's only two
												// bytes
		}

		byte maskingKey[] = null;
		// Testing wheter or not is's masked since the flag is in the RFC, 
		//but in our tests the payload from the client will always be masked.
		if (masked) {
			// Masking Key is the four bytes after payload length is read.
			maskingKey = new byte[4];
			inputStream.read(maskingKey);
		}

		// Payload itself, casting to int which probably is a lousy idea for large messages. 
		payload = new byte[(int) payloadLength];
		inputStream.read(payload);
		// Demask the byte array.
		if (masked) {
			for (int i = 0; i < payload.length; i++) {
				payload[i] ^= maskingKey[i % 4];
			}
		}

	}

	/**
	 * get a frame suitable for sending to client.
	 * payload array needs to be initialized/filled.
	 * @return Frame containing nonmasked data
	 */
	public byte[] getFrame() {

		int maskIndex = 0;
		// Make a 10 byte array to contain everything in the frame, 
		// not counting payload.
		byte[] frame = new byte[10];

		// Set first byte value, ignoring reserved bits.
		frame[0] = (byte) 129; // Final, text frame (10000001)

		// Set payload size
		if (payload.length <= 125) {
			frame[1] = (byte) payload.length;
			maskIndex = 2;
		} else if (payload.length >= 126 && payload.length <= 65535) {
			frame[1] = (byte) 126;
			int len = payload.length;
			frame[2] = (byte) ((len >> 8) & 0xff);
			frame[3] = (byte) (len & 0xff);
			maskIndex = 4;
		} else {
			frame[1] = (byte) 127;
			int len = payload.length;
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

		int totalLength = maskIndex + payload.length;

		byte[] reply = new byte[totalLength];

		
		int replyIndex = 0;
		for (int i = 0; i < maskIndex; i++) {
			reply[replyIndex] = frame[i];
			replyIndex++;
		}
		for (int i = 0; i < payload.length; i++) {
			reply[replyIndex] = payload[i];
			replyIndex++;
		}

		return reply;

	}

	/**
	 * Use this to check if the frame is final.
	 * @return true if frame is final.
	 */
	public boolean isFin() {
		return fin;
	}
	
	/**
	 * Use this to check if the frame is a disconnect frame
	 * @return true if opcode == 8, meaning disconnect.
	 */
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

	/**
	 * Convenience method for displaying payload data.
	 * @return payload decoded as UTF-8 String.
	 */
	public String getPayloadAsString() {
		return new String(payload, Charset.forName("UTF-8"));
	}

}
