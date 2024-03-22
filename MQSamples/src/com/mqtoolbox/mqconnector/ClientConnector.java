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

import java.net.MalformedURLException;
import java.net.URL;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

/**
 * Manage a client connection to a queue manager
 * 
 */
public class ClientConnector extends Connector {

	private MQQueueManager queueManager = null;
	private String queueManagerName = "";
	private URL clientChannelDefinitionTable = null;

	/**
	 * Connect as a client using the specified connection details
	 * 
	 * @param qmgrName
	 *                    Queue manager to connect to
	 * @param channelName
	 *                    SVRCONN channel name
	 * @param hostName
	 *                    Hostname or IP of the server hosting the queue manager
	 * @param port
	 *                    Listener port
	 * @param jksFileName
	 *                    (Optional) For SSL connections specify the JKS file name (e.g. c://mq/ssl/user.jks). For non-SSL
	 *                    connections specify null.
	 * @param jksPassword
	 *                    (Optional) For SSL connections specify the JKS file password. For non-SSL connections specify
	 *                    null.
	 * @throws MQException
	 */
	public ClientConnector(String qmgrName, String channelName, String hostName, int port, String jksFileName, String jksPassword)
			throws MQException {
		this.connect(qmgrName, channelName, hostName, port, jksFileName, jksPassword);
	}

	/**
	 * Connect as a client using the specified connection details
	 * 
	 * @param qmgrName
	 *                    Queue manager to connect to
	 * @param channelName
	 *                    SVRCONN channel name
	 * @param hostName
	 *                    Hostname or IP of the server hosting the queue manager
	 * @param port
	 *                    Listener port
	 * @param jksFileName
	 *                    (Optional) For SSL connections specify the JKS file name (e.g. c://mq/ssl/user.jks). For non-SSL
	 *                    connections specify null.
	 * @param jksPassword
	 *                    (Optional) For SSL connections specify the JKS file password. For non-SSL connections specify
	 *                    null.
	 * @throws NumberFormatException
	 * @throws MQException
	 */
	public ClientConnector(String qmgrName, String channelName, String hostName, String port, String jksFileName, String jksPassword)
			throws NumberFormatException, MQException {
		this.connect(qmgrName, channelName, hostName, port, jksFileName, jksPassword);
	}

	/**
	 * Connect as a client using a client channel definition table
	 * 
	 * @param qmgrName
	 *                                     Queue manager to connect to
	 * @param clientChannelDefinitionTable
	 *                                     URL of the CCDT. Example, "file:///c:/mq/AMQCLCHL.TAB"
	 * @param jksFileName
	 *                                     (Optional) For SSL connections specify the JKS file name (e.g.
	 *                                     c://mq/ssl/user.jks). For non-SSL
	 *                                     connections specify null.
	 * @param jksPassword
	 *                                     (Optional) For SSL connections specify the JKS file password. For non-SSL
	 *                                     connections specify null.
	 * @throws MQException
	 */
	public ClientConnector(String qmgrName, URL clientChannelDefinitionTable, String jksFileName, String jksPassword) throws MQException {
		this.connect(qmgrName, clientChannelDefinitionTable, jksFileName, jksPassword);
	}

	/**
	 * Connect as a client using a client channel definition table
	 * 
	 * @param qmgrName
	 *                                     Queue manager to connect to
	 * @param clientChannelDefinitionTable
	 *                                     URL of the CCDT. Example, "file:///c:/mq/AMQCLCHL.TAB"
	 * @param jksFileName
	 *                                     (Optional) For SSL connections specify the JKS file name (e.g.
	 *                                     c://mq/ssl/user.jks). For non-SSL
	 *                                     connections specify null.
	 * @param jksPassword
	 *                                     (Optional) For SSL connections specify the JKS file password. For non-SSL
	 *                                     connections specify null.
	 * @throws MQException
	 * @throws MalformedURLException
	 */
	public ClientConnector(String qmgrName, String clientChannelDefinitionTableURL, String jksFileName, String jksPassword)
			throws MQException, MalformedURLException {
		this.connect(qmgrName, clientChannelDefinitionTableURL, jksFileName, jksPassword);
	}

	/**
	 * Connect as a client using a client channel definition table
	 * 
	 * @param qmgrName
	 *                                     Queue manager to connect to
	 * @param clientChannelDefinitionTable
	 *                                     URL of the CCDT. Example, "file:///c:/mq/AMQCLCHL.TAB"
	 * @param clientChannelDefinitionTable
	 * @throws MQException
	 * @throws MalformedURLException
	 */
	private final void connect(String qmgrName, String clientChannelDefinitionTable, String jksFileName, String jksPassword)
			throws MQException, MalformedURLException {
		this.connect(qmgrName, new URL(clientChannelDefinitionTable.trim()), jksFileName, jksPassword);
	}

	/**
	 * 
	 * @param qmgrName
	 * @param channelName
	 * @param hostName
	 * @param port
	 * @param jksFileName
	 *                    (Optional) For SSL connections specify the JKS file name (e.g. c://mq/ssl/user.jks). For non-SSL
	 *                    connections specify null.
	 * @param jksPassword
	 *                    (Optional) For SSL connections specify the JKS file password. For non-SSL connections specify
	 *                    null.
	 * @throws NumberFormatException
	 * @throws MQException
	 */
	private final void connect(String qmgrName, String channelName, String hostName, String port, String jksFileName, String jksPassword)
			throws NumberFormatException, MQException {
		this.connect(qmgrName, channelName, hostName, Integer.parseInt(port), jksFileName, jksPassword);
	}

	/**
	 * Connect as a client using a client channel definition table
	 * 
	 * @param qmgrName
	 *                                     Queue manager to connect to
	 * @param clientChannelDefinitionTable
	 *                                     URL of the CCDT. Example, "file:///c:/mq/AMQCLCHL.TAB"
	 * @param jksFileName
	 *                                     (Optional) For SSL connections specify the JKS file name (e.g.
	 *                                     c://mq/ssl/user.jks). For non-SSL
	 *                                     connections specify null.
	 * @param jksPassword
	 *                                     (Optional) For SSL connections specify the JKS file password. For non-SSL
	 *                                     connections specify null.
	 * @param clientChannelDefinitionTable
	 * @throws MQException
	 */
	public final void connect(String qmgrName, URL clientChannelDefinitionTable, String jksFileName, String jksPassword) throws MQException {
		MQEnvironment.userID = System.getProperty("user.name");
		this.setSSLKeystore(jksFileName, jksPassword);
		this.queueManagerName = qmgrName.trim();
		this.clientChannelDefinitionTable = clientChannelDefinitionTable;
		this.connect();
		// qmgr = new MQQueueManager(qmgrName, clientChannelDefinitionTable);
	}

	/**
	 * Connect as a client using the specified connection details
	 * 
	 * @param qmgrName
	 *                    Queue manager to connect to
	 * @param channelName
	 *                    SVRCONN channel name
	 * @param hostName
	 *                    Hostname or IP of the server hosting the queue manager
	 * @param port
	 *                    Listener port
	 * @param jksFileName
	 *                    (Optional) For SSL connections specify the JKS file name (e.g. c://mq/ssl/user.jks). For non-SSL
	 *                    connections specify null.
	 * @param jksPassword
	 *                    (Optional) For SSL connections specify the JKS file password. For non-SSL connections specify
	 *                    null.
	 * @throws MQException
	 */
	private final void connect(String qmgrName, String channelName, String hostName, int port, String jksFileName, String jksPassword)
			throws MQException {
		MQEnvironment.userID = System.getProperty("user.name");
		MQEnvironment.channel = channelName;
		MQEnvironment.hostname = hostName;
		MQEnvironment.port = port;
		this.setSSLKeystore(jksFileName, jksPassword);
		this.queueManagerName = qmgrName.trim();
		this.connect();
		// qmgr = new MQQueueManager(qmgrName);
	}

	private void connect() throws MQException {
		if (this.clientChannelDefinitionTable != null) {
			this.queueManager = new MQQueueManager(this.queueManagerName, this.clientChannelDefinitionTable);
		} else {
			this.queueManager = new MQQueueManager(this.queueManagerName);
		}
	}

	public final void reconnect() throws MQException {
		this.connect();
	}

	/**
	 * Set the SSL keystore
	 * 
	 * @param jksFileName
	 * @param jksPassword
	 */
	private final void setSSLKeystore(String jksFileName, String jksPassword) {
		if (jksFileName == null || jksPassword == null) {
			return;
		}

		System.setProperty("javax.net.ssl.trustStore", jksFileName);
		System.setProperty("javax.net.ssl.trustStorePassword", jksPassword);

		System.setProperty("javax.net.ssl.keyStore", jksFileName);
		System.setProperty("javax.net.ssl.keyStorePassword", jksPassword);
	}

	/**
	 * Disconnect from the queue manager
	 * 
	 * @throws MQException
	 */
	public final void disconnect() throws MQException {
		this.queueManager.disconnect();
	}

	/**
	 * Return the queue manager object
	 * 
	 * @return
	 */
	public final MQQueueManager getQueueManager() {
		return queueManager;
	}

	public String getQueueManagerName() {
		try {
			return this.queueManager.getName();
		} catch (MQException e) {
			return "";
		}
	}

}
