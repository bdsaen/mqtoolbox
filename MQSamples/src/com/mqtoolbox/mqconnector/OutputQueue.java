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
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.MQConstants;

/**
 * Manage an output queue; that is a queue used to put messages.
 * 
 * @author Sean
 * 
 */
public class OutputQueue {
	public final int EXPIRY_UNLIMITED = -1;

	// private MQQueueManager qmgr = null;

	private Connector connector = null;

	private MQQueue queue = null;

	private MQMessage message = null;

	private String queueName = "";

	private String remoteQueueManagerName = "";

	private String overrideMessageFormat = null;

	/**
	 * Create a new Output Queue. The queue is automatically opened.
	 * 
	 * @param qmgr
	 *                  Queue manager connection
	 * @param queueName
	 *                  Name of queue to access
	 * @throws MQException
	 */
	public OutputQueue(Connector connector, String queueName) throws MQException {
		this.connector = connector;
		this.queueName = queueName;
		this.remoteQueueManagerName = "";
		this.openQueueForOutput(queueName);
	}

	/**
	 * Create a new Output Queue, where the output queue is defined on another queue manager; that is different to the
	 * connected-to queue
	 * manager. The queue is automatically opened.
	 * 
	 * @param connectedQmgr
	 *                               Queue manager connected to
	 * @param queueName
	 *                               Name of queue to access
	 * @param remoteQueueManagerName
	 *                               The queue is defined on a remote queue manager; that is not the connected-to queue
	 *                               manager
	 * @throws MQException
	 */
	public OutputQueue(Connector connector, String queueName, String remoteQueueManagerName) throws MQException {
		this.connector = connector;
		this.queueName = queueName;
		this.remoteQueueManagerName = remoteQueueManagerName;
		this.openQueueForOutput(queueName, remoteQueueManagerName);
	}

	/**
	 * Send a request message
	 * 
	 * @param requestMessage
	 *                         The 'request' message to send
	 * @param replyToQueueName
	 *                         The queue to which the receiving application should send the reply messages. The queue
	 *                         manager is set to the connected
	 *                         queue manager.
	 * @param expiryTime
	 *                         Expiry time in 1/10ths second of the request message
	 * @throws IOException
	 * @throws MQException
	 */
	public void sendRequestMessage(byte[] requestMessage, String replyToQueueName, int expiryTime) throws IOException, MQException {
		// Init
		this.message = new MQMessage();

		// Write the message
		this.message.messageType = MQConstants.MQMT_REQUEST;
		this.message.replyToQueueName = replyToQueueName;
		if (this.overrideMessageFormat != null)
			this.message.format = this.overrideMessageFormat;
		else
			this.message.format = MQConstants.MQFMT_STRING;
		this.message.expiry = expiryTime;
		this.message.messageId = MQConstants.MQMI_NONE;
		this.message.correlationId = MQConstants.MQCI_NONE;
		this.message.write(requestMessage);

		// Put the message
		this.put();

		// Reset these after the put
		this.overrideMessageFormat = null;
	}

	public void sendRequestMessage(MQMessage requestMessage, String replyToQueueName, int expiryTime) throws IOException, MQException {
		// Init
		this.message = requestMessage;

		// Put the message
		this.put();
	}

	/**
	 * Send a reply message
	 * 
	 * @param replyMessage
	 *                         The 'reply' message to send
	 * @param expiryTime
	 *                         Expiry time in 1/10ths second of the request message
	 * @param setCorrelationId
	 *                         The correlation id to set in the 'reply' message
	 * @throws IOException
	 * @throws MQException
	 */
	public void sendReplyMessage(byte[] replyMessage, int expiryTime, byte[] setCorrelationId) throws IOException, MQException {
		// Init
		this.message = new MQMessage();

		// Write the message
		this.message.messageType = MQConstants.MQMT_REPLY;
		if (this.overrideMessageFormat != null)
			this.message.format = this.overrideMessageFormat;
		else
			this.message.format = MQConstants.MQFMT_STRING;
		this.message.expiry = expiryTime;
		this.message.messageId = MQConstants.MQMI_NONE;
		this.message.correlationId = setCorrelationId;
		this.message.write(replyMessage);

		// Put the message
		this.put();
		// this.queue.put(this.message);

		// Reset these after the put
		this.overrideMessageFormat = null;
	}

	public void sendDatagramMessage(byte[] datagramMessage, int expiryTime) throws IOException, MQException {
		// Init
		this.message = new MQMessage();

		// Write the message
		this.message.messageType = MQConstants.MQMT_DATAGRAM;
		if (this.overrideMessageFormat != null)
			this.message.format = this.overrideMessageFormat;
		else
			this.message.format = MQConstants.MQFMT_STRING;
		this.message.expiry = expiryTime;
		this.message.messageId = MQConstants.MQMI_NONE;
		this.message.correlationId = MQConstants.MQCI_NONE;
		this.message.write(datagramMessage);

		// Put the message
		this.put();

		// Reset these after the put
		this.overrideMessageFormat = null;
	}

	public void sendDatagramMessage(MQMessage datagramMessage) throws IOException, MQException {
		// Init
		this.message = datagramMessage;

		// Put the message
		this.put();
	}

	/**
	 * Put the message
	 * 
	 * @throws MQException
	 */
	private void put() throws MQException {
		try {
			this.queue.put(this.message);
		} catch (MQException e) {
			if (e.reasonCode == MQConstants.MQRC_CONNECTION_BROKEN) {
				try {
					this.connector.reconnect();
				} catch (MQException e2) {
					throw (e2);
				}
				try {
					this.queue.put(this.message);
				} catch (MQException e1) {
					throw (e1);
				}
			} else {
				throw (e);
			}
		}
	}

	/**
	 * Open the queue for output
	 * 
	 * @throws MQException
	 */
	private final void openQueueForOutput(String queueName) throws MQException {
		int options = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;
		queue = this.connector.getQueueManager().accessQueue(queueName, options);
	}

	/**
	 * Open the queue for output on a different queue manager to the connected-to queue manager
	 * 
	 * @throws MQException
	 */
	private final void openQueueForOutput(String queueName, String targetQueueManagerName) throws MQException {
		int options = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;
		queue = this.connector.getQueueManager().accessQueue(queueName, options, targetQueueManagerName, null, null);
	}

	/**
	 * Close the queue
	 * 
	 */
	public final void close() {
		try {
			this.queue.close();
			this.queue = null;
		} catch (MQException e) {
			// Ignore any errors
		}
	}

	/**
	 * Return the remote queue manager name
	 * 
	 * @return
	 */
	public String getRemoteQueueManagerName() {
		return remoteQueueManagerName;
	}

	/**
	 * Return the queue name
	 * 
	 * @return
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Return the queue manager name
	 * 
	 * @return
	 */
	public String getQueueManagerName() {
		try {
			return this.connector.getQueueManager().getName();
		} catch (MQException e) {
			return "";
		}
	}

	/**
	 * Return the message put
	 * 
	 * @return
	 */

	public MQMessage getSentMessage() {
		return message;
	}

	/**
	 * Set the MQMD message format to the passed value
	 * 
	 * @param overrideFormat
	 */
	public void setOverrideFormat(String overrideFormat) {
		this.overrideMessageFormat = overrideFormat;
	}
}
