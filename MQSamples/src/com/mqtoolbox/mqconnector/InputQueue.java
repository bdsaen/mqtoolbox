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
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;

/**
 * Manage an input queue; that is a queue used to get messages.
 * 
 * @author Sean
 * 
 */
public class InputQueue {
	protected Connector connector;

	protected MQQueue queue = null;

	protected String queueName = "";

	protected String modelQueueName = "";

	protected MQMessage message = null; // last got message

	protected boolean noMessageFound = false;

	public final int WAIT_NONE = 0;

	public final int WAIT_5_SECONDS = 5000;

	public final int WAIT_30_SECONDS = 30000;

	public final int WAIT_UNLIMITED = -1;

	/**
	 * Create a new Input Queue
	 * 
	 * @param qmgr
	 *                  Queue manager connection
	 * @param queueName
	 *                  Name of queue to access
	 */
	public InputQueue(Connector connector, String queueName) {
		this.connector = connector;
		this.queueName = queueName;
		this.modelQueueName = "";
	}

	/**
	 * Open a dynamic input queue
	 * 
	 * @param qmgr
	 * @param queueName
	 * @param modelQueueName
	 */
	public InputQueue(Connector connector, String queueName, String modelQueueName) {
		this.connector = connector;
		this.queueName = queueName;
		this.modelQueueName = modelQueueName;
	}

	/**
	 * Close the queue
	 * 
	 */
	public final void close() {
		try {
			this.queue.close();
			this.queue = null;
			// this.queueOpenedForBrowse = false;
			// this.queueOpenedForGet = false;
		} catch (MQException e) {
			// Ignore any errors
		}
	}

	/**
	 * Was a message found?
	 * 
	 * @return
	 */
	public boolean noMessageFound() {
		return this.noMessageFound;
	}

	/**
	 * Return the queue object
	 * 
	 * @return
	 */
	public final MQQueue getQueue() {
		return this.queue;
	}

	/**
	 * Return the message object
	 * 
	 * @return
	 */
	public MQMessage getMessage() {
		return this.message;
	}
}
