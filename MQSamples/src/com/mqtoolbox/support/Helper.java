package com.mqtoolbox.support;

import java.io.IOException;

import com.ibm.mq.MQMessage;

/**
 * Some useful methods
 * 
 * @author Sean
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
	public static final byte[] extractMessagePayloadAsByteUTF8(MQMessage msg) {
		byte[] payload = null;
		try {
			payload = new byte[msg.getMessageLength()*2];
			int i =0;
			while (msg.getDataLength() != 0)
				payload[i++]=msg.readByte();
		} catch (IOException e) {
			return null;
		}
		return payload;
	}
}
