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

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.MQConstants;

public class InputQueueGet extends InputQueue {

	public InputQueueGet(Connector connector, String queueName) throws MQException, Exception{
		super(connector, queueName);
		this.openForGet();
	}

	public InputQueueGet(Connector connector, String queueName, String modelQueueName) throws MQException, Exception {
		super(connector, queueName,modelQueueName);
		this.openDynamicQueueForGet();
	}

	private final void openForGet() throws MQException, Exception {
		int options = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_FAIL_IF_QUIESCING;
		queue = this.connector.getQueueManager().accessQueue(this.queueName, options);
		// this.queueOpenedForGet = true;
	}

	private final void openDynamicQueueForGet() throws MQException, Exception {
		int options = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_FAIL_IF_QUIESCING;
		queue = this.connector.getQueueManager().accessQueue(super.modelQueueName, options, "", this.queueName, "");
		// this.queueOpenedForGet = true;
	}

	/**
	 * Get the next message on the queue, using the MQ convert option. The queue will be automatically opened if it is
	 * not already open and the first message got.
	 * 
	 * @param waitForMessages
	 *            Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *            How long to wait (milliseconds) for new messages to arrive
	 * @param matchCorrelationId
	 *            Get the message matching on correlation id. If the value is null, the correlation id is not used.
	 * @return
	 * @throws Exception
	 */
	public final MQMessage getNextWithConvert(boolean waitForMessages, int waitInterval, byte[] matchCorrelationId) throws MQException,
			Exception {
		return this.getMessage(true, false, waitForMessages, waitInterval, matchCorrelationId);
	}

	/**
	 * Get the next message on the queue. Do not use the MQ convert option. The queue will be automatically opened if it
	 * is not already open and the first message got.
	 * 
	 * @param waitForMessages
	 *            Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *            How long to wait (milliseconds) for new messages to arrive
	 * @param matchCorrelationId
	 *            Get the message matching on correlation id. If the value is null, the correlation id is not used.
	 * @return
	 * @throws Exception
	 */
	public final MQMessage getNextNoConvert(boolean waitForMessages, int waitInterval, byte[] matchCorrelationId) throws MQException,
			Exception {
		return this.getMessage(false, false, waitForMessages, waitInterval, matchCorrelationId);
	}

	/**
	 * Get the next message on the queue. The queue will be automatically opened if it is not already open.
	 * 
	 * @param convertMessage
	 *            Specify the MQ convert option?
	 * @param bypassFormatError
	 *            If a message format error is encountered, should the message be returned unconverted (bypassing the
	 *            format error)?
	 * @param waitForMessages
	 *            Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *            How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws MQException
	 */
	public final MQMessage getNextMessage(boolean convertMessage, boolean bypassFormatError, boolean waitForMessages, int waitInterval)
			throws MQException, Exception {
		return this.getMessage(convertMessage, bypassFormatError, waitForMessages, waitInterval, null);
	}

	/**
	 * Get the next message on the queue, matching on correlation id. The queue will be automatically opened if it is
	 * not already open.
	 * 
	 * @param convertMessage
	 *            Specify the MQ convert option?
	 * @param bypassFormatError
	 *            If a message format error is encountered, should the message be returned unconverted (bypassing the
	 *            format error)?
	 * @param waitForMessages
	 *            Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *            How long to wait (milliseconds) for new messages to arrive
	 * @param matchCorrelationId
	 *            Get the message matching on correlation id. If the value is null, the correlation id is not used.
	 * @return
	 * @throws MQException
	 */
	public final MQMessage getMessageByCorrelationId(boolean convertMessage, boolean bypassFormatError, boolean waitForMessages,
			int waitInterval, byte[] matchCorrelationId) throws MQException, Exception {
		return this.getMessage(convertMessage, bypassFormatError, waitForMessages, waitInterval, matchCorrelationId);
	}

	/**
	 * Get the next message on the queue. The queue will be automatically opened if it is not already open.
	 * 
	 * @param convertMessage
	 *            Specify the MQ convert option?
	 * @param bypassFormatError
	 *            If a message format error is encountered, should the message be returned unconverted (bypassing the
	 *            format error)?
	 * @param waitForMessages
	 *            Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *            How long to wait (milliseconds) for new messages to arrive
	 * @param matchCorrelationId
	 *            Get the message matching on correlation id. If the value is null, the correlation id is not used.
	 * @return
	 * @throws MQException
	 */
	private final MQMessage getMessage(boolean convertMessage, boolean bypassFormatError, boolean waitForMessages, int waitInterval,
			byte[] matchCorrelationId) throws MQException, Exception {

		// If the queue is not open, auto open it. Note: If it is already opened but not for get an exception will be
		// thrown below.
		if (queue == null) {
			this.openForGet();
		}

		// Initalize
		this.noMessageFound = false;
		this.message = new MQMessage();
		this.message.messageId = MQConstants.MQMI_NONE;
		if (matchCorrelationId == null) {
			this.message.correlationId = MQConstants.MQCI_NONE;
		} else {
			this.message.correlationId = matchCorrelationId;
		}
		MQGetMessageOptions gmo = new MQGetMessageOptions();

		// Get the next message
		try {
			gmo.options = MQConstants.MQGMO_FAIL_IF_QUIESCING;
			if (convertMessage) {
				gmo.options |= MQConstants.MQGMO_CONVERT;
			}
			if (waitForMessages) {
				gmo.options |= MQConstants.MQGMO_WAIT;
				gmo.waitInterval = waitInterval;
			}

			this.queue.get(this.message, gmo);

		} catch (MQException e) {
			if (e.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) {
				this.noMessageFound = true;
				return null;
			} else if (e.reasonCode == MQConstants.MQRC_FORMAT_ERROR && bypassFormatError) {
				// If we are bypassing any format error and a format error is encountered, browse the message
				// unconverted
				gmo.options = MQConstants.MQGMO_FAIL_IF_QUIESCING; // no need to wait
				try {
					this.queue.get(this.message, gmo);
				} catch (MQException e1) {
					throw e1;
				}
			} else {
				throw e;
			}
		}

		return this.message;
	}

	/**
	 * Browse and print all messages to the console
	 * 
	 * @throws MQException
	 * 
	 */
	public final void getAndPrintAllMessages() throws MQException, Exception {
		while (true) {
			try {
				this.getNextWithConvert(true, this.WAIT_5_SECONDS, null);
				if (this.noMessageFound) {
					break;
				} else {
					System.out.println(Helper.extractMessagePayload(this.message));
				}
			} catch (MQException e) {
				throw e;
			}
		}
	}

}
