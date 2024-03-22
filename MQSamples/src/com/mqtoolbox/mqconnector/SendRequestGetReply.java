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

/**
 * Manage sending a request message and getting the matching reply message
 * 
 * @author Sean
 * 
 */
public class SendRequestGetReply {

	private byte[] requestMessageId = null;

	private Connector connector = null;

	private InputQueueGet inputQueue = null;

	private OutputQueue outputQueue = null;

	public String getRequestQueueName() {
		return requestQueueName;
	}

	public String getReplyToQueueName() {
		return replyToQueueName;
	}

	private String requestQueueName = null;
	private String replyToQueueName = null;

	/**
	 * Create a new object
	 * 
	 * @param connector
	 * @throws Throwable 
	 */
	public SendRequestGetReply(Connector connector, String requestQueueName, String replyToQueueName) throws Throwable {
		this.connector = connector;
		this.requestQueueName = requestQueueName;
		this.replyToQueueName = replyToQueueName;
		this.openInputQueue();
		this.openOutputQueue();
	}

	public SendRequestGetReply(Connector connector, String requestQueueName, String replyToQueueName, String modelReplyToQueueName) throws Exception {
		this.connector = connector;
		this.requestQueueName = requestQueueName;
		this.replyToQueueName = replyToQueueName;
		this.openInputQueue(modelReplyToQueueName);
		this.replyToQueueName=this.inputQueue.getQueue().getName().trim();
		this.openOutputQueue();
	}

	/**
	 * Send a request message and wait for the corresponding reply. Use MQConvert in the get, do not bypass message
	 * format errors. The reply message 'correlation id' is matched to the request message 'message id'.
	 * 
	 * @param requestQueueName
	 *            Send a 'request' message to this queue
	 * @param replyToQueueName
	 *            Request 'reply' messages be returned to this queue, then wait for them to arrive
	 * @param expiryTime
	 *            Expiry time in 1/10ths second for the 'request' message
	 * @param waitInterval
	 *            Wait for a 'reply' message in 1/1000ths (milliseconds)
	 * @param requestMessage
	 *            The 'request' message to send
	 * @return
	 * @throws MQException
	 * @throws IOException
	 * @throws Exception
	 */
	public MQMessage sendRequestWaitReply(int expiryTime, int waitInterval, byte[] requestMessage) throws MQException, IOException,
			Exception {
		return this.sendRequestWaitReply(expiryTime, true, false, waitInterval, requestMessage);
	}

	/**
	 * Send a request message and wait for the corresponding reply
	 * 
	 * @param requestQueueName
	 *            Send a 'request' message to this queue
	 * @param replyToQueueName
	 *            Request 'reply' messages be returned to this queue, then wait for them to arrive
	 * @param expiryTime
	 *            Expiry time in 1/10ths second for the 'request' message
	 * @param convertMessage
	 *            Should the MQConvert option be specified when getting messages?
	 * @param bypassFormatError
	 *            If a message format error is encountered, should the get be retried without the MQConvert option to
	 *            try to get an unconverted message?
	 * @param waitInterval
	 *            Wait for a 'reply' message in 1/1000ths (milliseconds)
	 * @param requestMessage
	 *            The 'request' message to send
	 * @return
	 * @throws MQException
	 * @throws IOException
	 * @throws Exception
	 */
	public MQMessage sendRequestWaitReply(int expiryTime, boolean convertMessage, boolean bypassFormatError, int waitInterval,
			byte[] requestMessage) throws MQException, IOException, Exception {
		this.sendRequestMessage(expiryTime, requestMessage);
		// this.simulateReplyMessage(requestQueueName, replyToQueueName);
		return this.getReplyMessage(convertMessage, bypassFormatError, waitInterval, this.requestMessageId);

	}

	/**
	 * Send a request message
	 * 
	 * @param requestQueueName
	 *            Send a 'request' message to this queue
	 * @param replyToQueueName
	 *            Request 'reply' messages be returned to this queue, then wait for them to arrive
	 * @param expiryTime
	 *            Expiry time in 1/10ths second for the 'request' message
	 * @param requestMessage
	 *            The 'request' message to send
	 * @throws MQException
	 * @throws IOException
	 */
	public void sendRequestMessage(int expiryTime, byte[] requestMessage) throws MQException, IOException {

		// Open the queue if it is not already open
		if (this.outputQueue == null) {
			this.openOutputQueue();
		}

		// Init
		this.requestMessageId = null;

		// Send the message
		this.outputQueue.sendRequestMessage(requestMessage, this.replyToQueueName, expiryTime);

		// Store relevant fields
		this.requestMessageId = this.outputQueue.getSentMessage().messageId;
	}

	public void sendRequestMessage(int expiryTime, MQMessage requestMessage) throws MQException, IOException {

		// Open the queue if it is not already open
		if (this.outputQueue == null) {
			this.openOutputQueue();
		}

		// Init
		this.requestMessageId = null;

		// Send the message
		this.outputQueue.sendRequestMessage(requestMessage, this.replyToQueueName, expiryTime);

		// Store relevant fields
		this.requestMessageId = this.outputQueue.getSentMessage().messageId;
	}

	/**
	 * Get the reply message matching on correlation id
	 * 
	 * @param replyToQueueName
	 *            Request 'reply' messages be returned to this queue, then wait for them to arrive
	 * @param convertMessage
	 *            Should the MQConvert option be specified when getting messages?
	 * @param bypassFormatError
	 *            If a message format error is encountered, should the get be retried without the MQConvert option to
	 *            try to get an unconverted message?
	 * @param waitInterval
	 *            Wait for a 'reply' message in 1/1000ths (milliseconds)
	 * @param matchCorrelationId
	 * @return
	 * @throws MQException
	 * @throws Exception
	 */
	public MQMessage getReplyMessage(boolean convertMessage, boolean bypassFormatError, int waitInterval, byte[] matchCorrelationId)
			throws MQException, Exception {
		return this.inputQueue.getMessageByCorrelationId(convertMessage, bypassFormatError, true, waitInterval, matchCorrelationId);
	}

	/**
	 * Open the input queue
	 * 
	 * @param replyToQueueName
	 * @throws Throwable 
	 * @throws MQException 
	 */
	private void openInputQueue() throws MQException, Throwable {
		this.inputQueue = new InputQueueGet(this.connector, this.replyToQueueName);
	}

	/**
	 * Open the input queue
	 * 
	 * @param modelReplyToQueueName
	 * @throws MQException
	 * @throws Exception
	 */
	private void openInputQueue(String modelReplyToQueueName) throws MQException, Exception {
		this.inputQueue = new InputQueueGet(this.connector, this.replyToQueueName, modelReplyToQueueName);
	}

	/**
	 * Open the output queue
	 * 
	 * @param requestQueueName
	 * @throws MQException
	 */
	private void openOutputQueue() throws MQException {
		this.outputQueue = new OutputQueue(this.connector, this.requestQueueName);
	}

	/**
	 * Simply get the request message and bounce it back to the reply queue
	 * 
	 * @param requestQueueName
	 * @param replyToQueueName
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void simulateReplyMessage(String requestQueueName, String replyToQueueName) throws Exception {

		//
		InputQueueGet inputQueue = null;
		OutputQueue outputQueue = null;

		// Open the queue
		try {
			inputQueue = new InputQueueGet(this.connector, requestQueueName);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outputQueue = new OutputQueue(this.connector, replyToQueueName);

		// Get the request message
		inputQueue.getNextMessage(true, false, false, 0);

		// Simulate the reply message
		byte[] b = new byte[inputQueue.getMessage().getTotalMessageLength()];
		inputQueue.getMessage().readFully(b);
		outputQueue.sendReplyMessage(b, -1, inputQueue.getMessage().messageId);

		//
		inputQueue.close();
		inputQueue = null;

		outputQueue.close();
		outputQueue = null;
	}

	/**
	 * Close any opened queue
	 */
	public void close() {
		this.inputQueue.close();
		this.inputQueue = null;

		this.outputQueue.close();
		this.outputQueue = null;
	}
	
	public void setOverrideFormat(String overrideFormat) {
		if (this.outputQueue==null)
			return;
		this.outputQueue.setOverrideFormat(overrideFormat);
	}
}
