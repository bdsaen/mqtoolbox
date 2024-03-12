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
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

import java.net.MalformedURLException;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.TranslateSSLCipherSuite;

/**
 * Basic client connection to a queue manager using a Hashtable. This allows for SSL if required. Using a binary channel
 * TAB file as created by a queue manager when defining CLNTCONN channels. This feature was introduced in MQ 9.1.2 CD
 * and it is available in MQ 9.2 LTS and CD.
 *
 */
public class ClientConnection_HashtableUsingTAB_JSON_WithSSL {

	public static void main(String args[]) {
		ClientConnection_HashtableUsingTAB_JSON_WithSSL conn = new ClientConnection_HashtableUsingTAB_JSON_WithSSL();
		MQQueueManager qmgr1 = null;

		@SuppressWarnings("rawtypes")
		Hashtable props = new Hashtable<String, SSLSocketFactory>();

		System.out.println("Client connection using hashtable with a JSON TAB file using SSL");
		try {

			// Set the property to translate the SSL CiperSuite correctly depending on the Java provider (IBM or Oracle)
			TranslateSSLCipherSuite.setCipherMappings();

			// Add the SSL keystore
			Keystore ks = new Keystore("D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password", "D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password");
			ks.getSSL(props);
			qmgr1 = conn.connect("QMGR1", props, "file:AMQCLCHL_QMGR1_SSL.JSON");
			System.out.println("Connect to QMGR " + qmgr1.getName() + " - " + qmgr1.getDescription());

		} catch (MQException e) {
			System.out.println(
					String.format("MQ error details: %s(%s)\n\n%s", MQConstants.lookupReasonCode(e.getReason()), e.getReason(), e.getCause()));
		} catch (MalformedURLException e) {
			System.out.println(String.format("MQ error details: Invalid CCDT URL\n\n%s", e.getMessage(), e.getCause()));
		} finally {
			if (qmgr1 != null) {
				try {
					qmgr1.disconnect();
				} catch (MQException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Connect to the queue manager using a CCDT (client channel definition table)
	 * 
	 * @param qmgrName Queue manager to connect to
	 * @param ccdtStr  Client Channel Definition Table. Example, "file:///c:/mq/AMQCLCHL.TAB"
	 * @return
	 * @throws MQException
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("rawtypes")
	public final MQQueueManager connect(String qmgrName, Hashtable props, String ccdtStr) throws MQException, MalformedURLException {
		return new MQQueueManager(qmgrName, props, new URL(ccdtStr));
	}
}
