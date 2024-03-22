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

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.MQConstants;

/**
 * Open the queue to browse messages
 *
 */
public class InputQueueBrowse extends InputQueue {

	public InputQueueBrowse(Connector connector, String queueName) {
		super(connector, queueName);
	}

	public InputQueueBrowse(Connector connector, String queueName, String modelQueueName) {
		super(connector, queueName);
	}

	/**
	 * Open the queue for browsing messages
	 * 
	 * @throws MQException
	 */
	private final void openForBrowse() throws MQException {
		int options = MQConstants.MQOO_BROWSE | MQConstants.MQOO_FAIL_IF_QUIESCING;
		queue = this.connector.getQueueManager().accessQueue(this.queueName, options);
		// this.queueOpenedForBrowse = true;
	}

	/**
	 * Start browsing messages, using the MQ convert option. The queue will be automatically opened if it is not already
	 * open.
	 * 
	 * @param waitForMessages
	 *                        Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *                        How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws Exception
	 */
	public final MQMessage startBrowseWithConvert(boolean waitForMessages, int waitInterval) throws MQException, Exception {
		return this.startBrowse(true, false, waitForMessages, waitInterval);
	}

	/**
	 * Start browsing messages. Do not use the MQ convert option. The queue will be automatically opened if it is not
	 * already open.
	 * 
	 * @param waitForMessages
	 *                        Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *                        How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws Exception
	 */
	public final MQMessage startBrowseNoConvert(boolean waitForMessages, int waitInterval) throws MQException, Exception {
		return this.startBrowse(false, false, waitForMessages, waitInterval);
	}

	/**
	 * Start browsing messages. The queue will be automatically opened if it is not already open.
	 * 
	 * @param convertMessage
	 *                          Specify the MQ convert option?
	 * @param bypassFormatError
	 *                          If a message format error is encountered, should the message be returned unconverted
	 *                          (bypassing the format error)?
	 * @param waitForMessages
	 *                          Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *                          How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws MQException
	 * @throws Exception
	 */
	public final MQMessage startBrowse(boolean convertMessage, boolean bypassFormatError, boolean waitForMessages, int waitInterval)
			throws MQException, Exception {

		// Auto open queue if it is not open. Note: If it is already opened but not for browse an exception will be
		// thrown below.
		if (queue == null) {
			this.openForBrowse();
		}

		// Initialize
		this.noMessageFound = false;
		this.message = new MQMessage();
		;
		MQGetMessageOptions gmo = new MQGetMessageOptions();

		// Open queue
		try {
			gmo.options = MQConstants.MQGMO_BROWSE_FIRST | MQConstants.MQGMO_FAIL_IF_QUIESCING;
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
				// If we are bypassing any format error and a format error is encountered, browse the message unconverted
				gmo.options = MQConstants.MQGMO_BROWSE_FIRST | MQConstants.MQGMO_FAIL_IF_QUIESCING; // no need to wait
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
	 * Browse the next message on the queue, using the MQ convert option. The queue will be automatically opened if it is
	 * not already open
	 * and the start browse invoked.
	 * 
	 * @param waitForMessages
	 *                        Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *                        How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws Exception
	 */
	public final MQMessage browseNextWithConvert(boolean waitForMessages, int waitInterval) throws MQException, Exception {
		return this.browseNext(true, false, waitForMessages, waitInterval);
	}

	/**
	 * Browse the next message on the queue. Do not use the MQ convert option. . The queue will be automatically opened if
	 * it is not already
	 * open and the start browse invoked.
	 * 
	 * @param waitForMessages
	 *                        Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *                        How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws Exception
	 */
	public final MQMessage browseNextNoConvert(boolean waitForMessages, int waitInterval) throws MQException, Exception {
		return this.browseNext(false, false, waitForMessages, waitInterval);
	}

	/**
	 * Browse the next message on the queue. The queue will be automatically opened if it is not already open and the start
	 * browse invoked.
	 * 
	 * @param convertMessage
	 *                          Specify the MQ convert option?
	 * @param bypassFormatError
	 *                          If a message format error is encountered, should the message be returned unconverted
	 *                          (bypassing the format error)?
	 * @param waitForMessages
	 *                          Wait for new messages to arrive before returning no message found?
	 * @param waitInterval
	 *                          How long to wait (milliseconds) for new messages to arrive
	 * @return
	 * @throws Exception
	 */
	public final MQMessage browseNext(boolean convertMessage, boolean bypassFormatError, boolean waitForMessages, int waitInterval)
			throws MQException, Exception {

		// If the queue is not open, auto open it and start the browse. Note: If it is already opened but not for browse
		// an exception will
		// be thrown below.
		if (queue == null) {
			this.openForBrowse();
			return this.startBrowse(convertMessage, bypassFormatError, waitForMessages, waitInterval);
		}

		// Initalize
		this.noMessageFound = false;
		this.message = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();

		// Browse the next message
		try {
			gmo.options = MQConstants.MQGMO_BROWSE_NEXT | MQConstants.MQGMO_FAIL_IF_QUIESCING;
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
				gmo.options = MQConstants.MQGMO_BROWSE_NEXT | MQConstants.MQGMO_FAIL_IF_QUIESCING; // no need to wait
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
	public final void browseAndPrintAllMessages() throws MQException, Exception {
		while (true) {
			try {
				this.browseNextWithConvert(true, this.WAIT_5_SECONDS);
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

	/**
	 * Browse and print all messages returned without using the convert option
	 * 
	 * @throws MQException
	 * @throws Exception
	 */
	public final void browseAndPrintAllMessagesNoConvert() throws MQException, Exception {
		while (true) {
			try {
				this.browseNextNoConvert(true, this.WAIT_5_SECONDS);
				if (this.noMessageFound) {
					break;
				} else {
					byte[] payload = Helper.extractMessagePayloadAsByte(this.message);
					for (int i = 0; i < payload.length; i++) {
						System.out.print((char) payload[i]);
					}
					// System.out.println(Helper.extractMessagePayload(this.message));
					System.out.println("");
				}
			} catch (MQException e) {
				throw e;
			}
		}
	}

	/**
	 * Browse all messages and write to a file
	 * 
	 * @throws MQException
	 * @throws Exception
	 */
	public final void browseAndPrintAllMessagesToFile() throws MQException, Exception {

		DataOutputStream dos = null;
		
		try {
			dos = new DataOutputStream(new FileOutputStream("N:\\temp\\sean9.txt", true));

			while (true) {
				try {
					this.browseNextWithConvert(true, this.WAIT_5_SECONDS);
					if (this.noMessageFound) {
						break;
					} else {
						byte[] payload = Helper.extractMessagePayloadAsByte(this.message);
						dos.write(payload);
						dos.writeBytes("\n");
					}
				} catch (MQException e) {
					throw e;
				}
			}
		} finally {
			dos.close();
		}
	}
}
