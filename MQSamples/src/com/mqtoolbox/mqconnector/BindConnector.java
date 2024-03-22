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
import com.ibm.mq.MQQueueManager;

/**
 * Manage a bind connection to a queue manager
 * 
 */
public class BindConnector extends Connector {

	private String queueManagerName = "";
	private MQQueueManager qmgr = null;

	/**
	 * Create a new bind connection
	 * 
	 * @param qmgrName
	 *                 Queue manager name
	 * @throws MQException
	 */
	public BindConnector(String qmgrName) throws MQException {
		this.queueManagerName = qmgrName;
		this.connect();
	}

	/**
	 * Return the queue manager object
	 * 
	 * @return
	 */
	public final MQQueueManager getQueueManager() {
		return qmgr;
	}

	/**
	 * Connect to the queue manager
	 * 
	 * @throws MQException
	 */
	private void connect() throws MQException {
		this.qmgr = new MQQueueManager(this.queueManagerName);
	}

	/**
	 * Reconnect to the queue manager - just calls the connect
	 */
	public void reconnect() throws MQException {
		this.connect();
	}

	/**
	 * Disconnect from the queue manager
	 * 
	 * @throws MQException
	 */
	public final void disconnect() throws MQException {
		this.qmgr.disconnect();
	}

	public String getQueueManagerName() {
		return queueManagerName;
	}
}
