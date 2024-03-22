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

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

public class SendAndForget {

	private Connector connector = null;

	private OutputQueue outputQueue = null;

	private String targetQueueName = null;

	/**
	 * Put a send-and-forget message - that is, a datagram
	 * 
	 * @param connector
	 * @param targetQueueName
	 * @throws Throwable
	 */
	public SendAndForget(Connector connector, String targetQueueName) throws Throwable {
		this.connector = connector;
		this.targetQueueName = targetQueueName;
		this.openOutputQueue();
	}

	/**
	 * Open the output queue
	 * 
	 * @throws MQException
	 */
	private void openOutputQueue() throws MQException {
		this.outputQueue = new OutputQueue(this.connector, this.targetQueueName);
	}

	/**
	 * Put the datagram message
	 * 
	 * @param datagramMessage
	 * @throws MQException
	 * @throws IOException
	 */
	public void sendMessage(MQMessage datagramMessage) throws MQException, IOException {

		// Open the queue if it is not already open
		if (this.outputQueue == null) {
			this.openOutputQueue();
		}

		// Send the message
		this.outputQueue.sendDatagramMessage(datagramMessage);

		// Store relevant fields
	}

}
