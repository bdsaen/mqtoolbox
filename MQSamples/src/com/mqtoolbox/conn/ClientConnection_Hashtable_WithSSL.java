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

package com.mqtoolbox.conn;

import java.util.Hashtable;

import javax.net.ssl.SSLSocketFactory;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;
import com.mqtoolbox.support.TranslateSSLCipherSuite;

/**
 * Basic client connection to a queue manager using a Hashtable. This allows for SSL if required.
 *
 */
public class ClientConnection_Hashtable_WithSSL {

	public static void main(String args[]) {
		ClientConnection_Hashtable_WithSSL conn = new ClientConnection_Hashtable_WithSSL();
		MQQueueManager qmgr = null;
		StopWatch stopwatch = new StopWatch();

		System.out.println("Client connection using hashtable");
		try {
			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect"));
			qmgr = conn.connect("QMGR1", "TEST.SVRCONN.SSL", "localhost", 1414);
//			qmgr = conn.connect("QMGR2", "TEST.SVRCONN.SSL", "localhost", 1415);
			System.out.println(stopwatch.formatInProgressTimeTaken("After connect"));
			stopwatch.stop();

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before disconnect"));
			qmgr.disconnect();
			System.out.println(stopwatch.formatInProgressTimeTaken("After disconnect"));
			stopwatch.stop();
		} catch (MQException e) {
			System.out.println(
					String.format("MQ error details: %s(%s)\n\n%s", MQConstants.lookupReasonCode(e.getReason()), e.getReason(), e.getCause()));
		}
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

		// Add the SSL keystore
		Keystore ks = new Keystore("D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password", "D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password");
		ks.getSSL(props);
		
		// Set the SSL CipherSuite
		props.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, TranslateSSLCipherSuite.SSLCipherSuite.ECDHE_RSA_AES_256_GCM_SHA384.getValue());

		// Connect
		return new MQQueueManager(qmgrName, props);
	}
}
