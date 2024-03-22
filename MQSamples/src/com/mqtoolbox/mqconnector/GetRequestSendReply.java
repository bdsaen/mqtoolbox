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

import java.util.ArrayList;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;

/**
 * Class to support the request-reply pattern. This class contains methods to get a request message and send reply
 * messages.
 *
 */
public class GetRequestSendReply {
	public final int WAIT_NONE = 0;

	public final int WAIT_5_SECONDS = 5000;

	public final int WAIT_30_SECONDS = 30000;

	public final int WAIT_UNLIMITED = -1;

	public final int EXPIRY_UNLIMITED = -1;

	public final int EXPIRY_30_SECONDS = 300;

	private Connector connector = null;

	private InputQueueGet inputQueue = null;

	// List of reply queues. This allows the queue to remain open during the life of the program (should the reply queue
	// or queue manager change).
	private ArrayList<ReplyQueue> replyQueueList = null;

	/**
	 * Use a bind connection
	 * 
	 * @param bind
	 */
	public GetRequestSendReply(Connector connector) {
		this.connector = connector;
	}

	/**
	 * Get a request message from the input queue
	 * 
	 * @param requestQueueName Queue name to get messages from
	 * @param waitInterval     Wait time in milliseconds to wait for more messages to arrive to the input queue
	 * @return
	 * @throws MQException
	 * @throws Exception
	 */
	public MQMessage getRequestMessage(String requestQueueName, int waitInterval) throws MQException, Exception {
		return this.getRequestMessage(requestQueueName, true, false, waitInterval);
	}

	/**
	 * Get a request message from the input queue
	 * 
	 * @param requestQueueName  Queue name to get messages from
	 * @param convertMessage    Get the message with the convert option?
	 * @param bypassFormatError
	 * @param waitInterval      Wait time in milliseconds to wait for more messages to arrive to the input queue
	 * @return
	 * @throws MQException
	 * @throws Exception
	 */
	public MQMessage getRequestMessage(String requestQueueName, boolean convertMessage, boolean bypassFormatError, int waitInterval)
			throws MQException, Exception {

		// Open the queue if it is not already open
		if (this.inputQueue == null) {
			this.inputQueue = new InputQueueGet(this.connector, requestQueueName);
		}

		// Get the message
		return this.inputQueue.getNextMessage(convertMessage, bypassFormatError, true, waitInterval);
	}

	/**
	 * Put a reply message using the replyToQueueName and replyToQueueManagerName from the MQMD of the message just got.
	 * Prior to calling this method, ensure the getRequestMesasge has been called.
	 * 
	 * @param expiryTime   Set the expiry time in 1/10th's of a second
	 * @param replyMessage Reply message to put
	 * @throws Exception
	 */
	public void sendReplyMessage(int expiryTime, byte[] replyMessage) throws Exception {
		this.sendReplyMessage(this.inputQueue.getMessage().replyToQueueName, this.inputQueue.getMessage().replyToQueueManagerName, expiryTime,
				replyMessage, this.inputQueue.getMessage().messageId);
	}

	/**
	 * Put a reply message using the passed values.
	 * 
	 * @param replyQueueName        Put the reply message to this queue
	 * @param replyQueueManagerName Put the reply message to this queue manager - this will be different queue manager to
	 *                              the one you are connected to. This is generally the ReplyToQueueManger from the request
	 *                              message MQMD
	 * @param expiryTime            Set the expiry time in 1/10th's of a second
	 * @param replyMessage          Reply message to put
	 * @throws Exception
	 */
	public void sendReplyMessage(String replyQueueName, String replyQueueManagerName, int expiryTime, byte[] replyMessage) throws Exception {
		this.sendReplyMessage(replyQueueName, replyQueueManagerName, expiryTime, replyMessage, this.inputQueue.getMessage().messageId);

	}

	/**
	 * Put a reply message using the passed values.
	 * 
	 * @param replyQueueName        Put the reply message to this queue
	 * @param replyQueueManagerName Put the reply message to this queue manager - this will be different queue manager to
	 *                              the one you are connected to. This is generally the ReplyToQueueManger from the request
	 *                              messaged MQMD
	 * @param expiryTime            Set the expiry time in 1/10th's of a second
	 * @param replyMessage          Reply message to put
	 * @param correlationId         Set the correlation ID to this value. This is generally the correlationId from the
	 *                              request message MQMD
	 * @throws Exception
	 */
	public void sendReplyMessage(String replyQueueName, String replyQueueManagerName, int expiryTime, byte[] replyMessage, byte[] correlationId)
			throws Exception {

		// Validate parameters
		if (replyQueueName == null || replyQueueName.trim().matches("")) {
			throw new Exception("Reply queue name missing");
		}
		if (replyQueueManagerName == null || replyQueueManagerName.trim().matches("")) {
			throw new Exception("Reply queue manager name missing");
		}

		//
		ReplyQueue replyQueue = null;

		// Scan reply queue list
		if (this.replyQueueList == null) {
			// Start a new list
			this.replyQueueList = new ArrayList<ReplyQueue>(1); // most likely will have one reply queue
			this.openReplyQueue(replyQueueName, replyQueueManagerName);
			replyQueue = this.replyQueueList.get(this.replyQueueList.size() - 1);

		} else {
			// Has the queue already been opened?
			boolean queueAlreadyOpen = false;
			for (ReplyQueue rq : this.replyQueueList) {
				if (rq.getReplyQueueName().trim().matches(replyQueueName.trim())
						&& rq.getReplyQueueManagerName().trim().matches(replyQueueManagerName.trim())) {
					queueAlreadyOpen = true;
					replyQueue = rq;
					break;
				}
			}

			// No it hasn't
			if (!queueAlreadyOpen) {
				this.openReplyQueue(replyQueueName, replyQueueManagerName);
				replyQueue = this.replyQueueList.get(this.replyQueueList.size() - 1);
			}
		}

		// Send the reply message
		replyQueue.queue.sendReplyMessage(replyMessage, expiryTime, correlationId);
	}

	/**
	 * Open the reply queue for output
	 * 
	 * @param replyQueueName        Put the reply message to this queue
	 * @param replyQueueManagerName Put the reply message to this queue manager - this will be different queue manager to
	 *                              the one you are connected to. This is generally the ReplyToQueueManger from the request
	 *                              messaged MQMD
	 * @throws MQException
	 */
	private void openReplyQueue(String replyQueueName, String replyQueueManagerName) throws MQException {
		this.replyQueueList.add(new ReplyQueue(this.connector, replyQueueName, replyQueueManagerName));
	}

	/**
	 * Close all the open objects. This should be placed in a try finally{} section.
	 */
	public void terminate() {
		// Close input queues
		if (this.inputQueue != null) {
			this.inputQueue.close();
			this.inputQueue = null;
		}

		// Close reply queues
		if (this.replyQueueList != null) {
			for (ReplyQueue rq : this.replyQueueList) {
				rq.close();
			}
			this.replyQueueList = null;
		}

		// Destroy, but don't close any queue manager connections; they may still be needed elsewhere
		this.connector = null;
	}

	/**
	 * Return the input queue object
	 * 
	 * @return
	 */
	public InputQueue getInputQueue() {
		return inputQueue;
	}

	/**
	 * Supporting inner class
	 *
	 */
	class ReplyQueue {
		/**
		 * Create a new reply queue
		 * 
		 * @param connectedQmgr
		 * @param replyQueueName
		 * @param replyQueueManagerName
		 * @throws MQException
		 */
		ReplyQueue(Connector connector, String replyQueueName, String replyQueueManagerName) throws MQException {
			if (connector.getQueueManagerName().matches(replyQueueManagerName.trim())) {
				this.replyQueueManagerName = connector.getQueueManagerName();
				this.queue = new OutputQueue(connector, replyQueueName);
			} else {
				this.replyQueueManagerName = replyQueueManagerName;
				this.queue = new OutputQueue(connector, replyQueueName, replyQueueManagerName);
			}
		}

		private OutputQueue queue = null;

		private String replyQueueManagerName = "";

		public String getReplyQueueName() {
			return this.queue.getQueueName();
		}

		public String getReplyQueueManagerName() {
			return replyQueueManagerName;
		}

		public OutputQueue getQueue() {
			return this.queue;
		}

		public void close() {
			this.queue.close();
			this.queue = null;
		}
	}
}
