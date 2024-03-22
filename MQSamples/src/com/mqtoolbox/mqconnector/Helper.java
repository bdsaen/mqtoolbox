/**
 MIT License

Copyright (c) 2023 bdsaen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.mqtoolbox.mqconnector;

import java.io.IOException;

import com.ibm.mq.MQMessage;

/**
 * Some useful methods
 * 
 */
public class Helper {

	/**
	 * Convert a byte array to a HEX string
	 * 
	 * @param bytes
	 * @return
	 */
	public static final String toHex(byte[] bytes) {
		String hex = "";
		for (int i = 0; i < bytes.length; i++) {
			hex += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
		}
		return hex.toUpperCase();
	}

	/**
	 * Extract the message payload
	 * 
	 * @return
	 */
	public static final String extractMessagePayload(MQMessage msg) {
		String payload = "";

		try {
			payload = msg.readStringOfByteLength(msg.getDataLength());
		} catch (IOException e) {
			return null;
		}
		return payload;
	}

	/**
	 * Read the message payload as a byte[] array
	 * 
	 * @param msg The message to process
	 * @return
	 */
	public static final byte[] extractMessagePayloadAsByte(MQMessage msg) {
		byte[] payload = null;
		try {
			payload = new byte[msg.getMessageLength()];
			msg.readFully(payload);
		} catch (IOException e) {
			return null;
		}
		return payload;
	}

	/**
	 * Read the message payload as a byte[] array when processing a UTF-8 message
	 * 
	 * @param msg The message to process
	 * @return
	 */
	public static final byte[] extractMessagePayloadAsByteUTF8(MQMessage msg) {
		byte[] payload = null;
		try {
			payload = new byte[msg.getMessageLength() * 2];
			int i = 0;
			while (msg.getDataLength() != 0)
				payload[i++] = msg.readByte();
		} catch (IOException e) {
			return null;
		}
		return payload;
	}
}
