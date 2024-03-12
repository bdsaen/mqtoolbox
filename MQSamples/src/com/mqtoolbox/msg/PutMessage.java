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

package com.mqtoolbox.msg;

import java.io.IOException;
import java.util.Hashtable;

import javax.net.ssl.SSLSocketFactory;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
//import com.mqtoolbox.conn.Keystore;
//import com.mqtoolbox.support.TranslateSSLCipherSuite;

// To test this example, complete the following.
//
// Create queue managers QMGR1.
// 
//def ql('ZTEST.QMGR1.QL') -m QMGR1
//
public class PutMessage {

	public static void main(String[] args) {
		PutMessage example = new PutMessage();
		MQQueueManager qmgr = null;
		MQQueue queue = null;

		System.out.println("Send message to queue");
		try {
//			qmgr = example.connect("QMGR1", "TEST.SVRCONN.SSL", "localhost", 1414);
			qmgr = example.connect("QMGR1", "TEST.SVRCONN", "localhost", 1414);

			// PUT STYLE
			queue = example.openQueueForOutput(qmgr, "ZTEST.QMGR1.QL");
			example.putMessageBytes(queue, "hello".getBytes());

			// PUT1 style
			example.openPutClose(qmgr, "ZTEST.QMGR1.QL", "hello");
			example.openPutClose(qmgr, "ZTEST.QMGR1.QL", "hello");
			example.openPutClose(qmgr, "ZTEST.QMGR1.QL", "hello");
			example.openPutClose(qmgr, "ZTEST.QMGR1.QL", "hello");
			
		} catch (MQException e) {
			System.out.println(
					String.format("MQ error details: %s(%s)\n\n%s", MQConstants.lookupReasonCode(e.getReason()), e.getReason(), e.getCause()));
		} finally {
			if (queue != null) {
				try {
					queue.close();
				} catch (MQException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (qmgr != null) {
				try {
					qmgr.disconnect();
				} catch (MQException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Simulate a PUT1 - Open queue, put message, close queue
	 * 
	 * @param qmgr           Queue manager connected to
	 * @param queueName      Queue name on the remote queue manager that the message is to be delivered to
	 * @param payload        Message to send
	 */
	public final void openPutClose(MQQueueManager qmgr, String queueName, String payload) {
		MQQueue queue = this.openQueueForOutput(qmgr, queueName);
		this.putMessageString(queue, payload);
		if (queue != null)
			try {
				queue.close();
			} catch (MQException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	/**
	 * Open the queue for putting messages
	 * 
	 * @param qmgr      Queue manager connected to
	 * @param queueName Locally defined queue or cluster queue name
	 * @return
	 */
	public MQQueue openQueueForOutput(MQQueueManager qmgr, String queueName) {
		MQQueue queue = null;
		int openOptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;

		try {
			System.out.print("Opening queue for output " + queueName + ", ");
			queue = qmgr.accessQueue(queueName, openOptions);
		} catch (MQException e) {
			System.out.println("FAILED, MQ reason code " + e.getReason());
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		System.out.println("OK");
		return queue;
	}

	/**
	 * Put a message to the passed queue
	 * 
	 * @param queue   Queue name to send to
	 * @param payload Message to send
	 * @return
	 */
	public boolean putMessageBytes(MQQueue queue, byte[] payload) {
		MQMessage msg = new MQMessage();
		msg.messageType = MQConstants.MQMT_DATAGRAM;
		msg.format = MQConstants.MQFMT_STRING;
		try {
			System.out.print(String.format("Putting test message to queue %s, ", queue.getName().trim()));
			msg.messageType = MQConstants.MQMT_DATAGRAM;
			msg.format = MQConstants.MQFMT_STRING;
			msg.expiry = 600; // 60 seconds
			msg.write(payload);
			queue.put(msg);
		} catch (MQException e) {
			System.out.println("FAILED, MQ reason code " + e.getReason());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("FAILED, MQ reason code " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
		System.out.println("OK");
		return true;
	}

	/**
	 * Put a message to the passed queue
	 * 
	 * @param queue   Queue name to send to
	 * @param payload Message to send.
	 * @return
	 */
	public boolean putMessageString(MQQueue queue, String payload) {
		MQMessage msg = new MQMessage();
		msg.messageType = MQConstants.MQMT_DATAGRAM;
		// msg.replyToQueueName = "TEST.REPLY.QL";
		msg.format = MQConstants.MQFMT_STRING;
		msg.expiry = 600; // 60 seconds
		try {
			System.out.print(String.format("Putting test message to queue %s, ", queue.getName().trim()));
			msg.writeString(payload);
			queue.put(msg);
		} catch (IOException e) {
			System.out.println("FAILED, MQ reason code " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		} catch (MQException e) {
			System.out.println("FAILED, MQ reason code " + e.getReason());
			e.printStackTrace();
			return false;
		}
		System.out.println("OK");
		return true;
	}

	/**
	 * Return a queue manager object containing a connection to the queue manager. This method does not set the
	 * MQEnvironment variables. Rather it sets the properties in a Hashtable. This offers the most flexibility especially
	 * when connecting to multiple queue managers from the same process.
	 * 
	 * @param qmgrName    Name of queue manager to connect to
	 * @param channelName Name of an SVRCONN channel to connect to
	 * @param hostName    Host name or IP of the target queue manager
	 * @param port        Listener port of the target queue manager
	 * @return
	 * @throws MQException
	 */
	public final MQQueueManager connect(String qmgrName, String channelName, String hostName, int port) throws MQException {
		@SuppressWarnings("rawtypes")
		Hashtable props = new Hashtable<String, SSLSocketFactory>();
		props.put(MQConstants.USER_ID_PROPERTY, System.getProperty("user.name"));
		props.put("channel", channelName);
		props.put("hostname", hostName);
		props.put("port", port);

		// Set the property to translate the SSL CiperSuite correctly depending on the Java provider (IBM or Oracle)
//		TranslateSSLCipherSuite.setCipherMappings();

		// Add the SSL keystore
//		Keystore ks = new Keystore("D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password", "D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password");
//		ks.getSSL(props);

		// Set the SSL CipherSuite
//		props.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, TranslateSSLCipherSuite.SSLCipherSuite.ECDHE_RSA_AES_256_GCM_SHA384.getValue());

		// Connect
		return new MQQueueManager(qmgrName, props);
	}

}
