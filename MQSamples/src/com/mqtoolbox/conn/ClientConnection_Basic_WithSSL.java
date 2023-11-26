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

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;

/**
 * Basic client connection to a queue manager. Using MQEnvironment variables, while simple, is perhaps not the ideal
 * way.
 *
 */

//DEFINE CHANNEL('TEST.SVRCONN.SSL') CHLTYPE(SVRCONN) MCAUSER('noaccess')
//SET CHLAUTH ('TEST.SVRCONN.SSL') TYPE(USERMAP) DESCR('Allow') CLNTUSER('your_logged_in_name') MCAUSER('MUSR_MQADMIN') USERSRC(MAP)
//SET CHLAUTH ('TEST.SVRCONN.SSL') TYPE(BLOCKUSER) DESCR('Default block') USERLIST(noaccess)

public class ClientConnection_Basic_WithSSL {

	public static void main(String args[]) {
		ClientConnection_Basic_WithSSL conn = new ClientConnection_Basic_WithSSL();
		MQQueueManager qmgr = null;
		StopWatch stopwatch = new StopWatch();

		System.out.println("Basic client connection");
		try {
			conn.setSSLKeystore("D:\\Dev\\#SSL\\mqtoolbox\\client.jks", "password");

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect"));
			qmgr = conn.connect("QMGR1", "TEST.SVRCONN.SSL", "localhost", 1414, "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"); // Oracle style
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
	 * Return a queue manager object containing a connection to the queue manager
	 * 
	 * @param qmgrName       Name of queue manager to connect to
	 * @param channelName    Name of an SVRCONN channel to connect to
	 * @param hostName       Host name or IP of the target queue manager
	 * @param port           Listener port of the target queue manager
	 * @param sslCipherSuite SSL CipherSuite to use. The actual value depends on whether you are using IBM Java or Oracle
	 *                       java. This method will automatically set com.ibm.mq.cfg.useIBMCipherMappings depending on the
	 *                       Java provider (IBM or Oracle). To override this auto-detection, you can pass in your own value
	 *                       as -Dcom.ibm.mq.cfg.useIBMCipherMappings=true or false
	 * @return
	 * @throws MQException
	 */
	public final MQQueueManager connect(String qmgrName, String channelName, String hostName, int port, String sslCipherSuite) throws MQException {
		MQEnvironment.userID = System.getProperty("user.name");
		MQEnvironment.channel = channelName;
		MQEnvironment.hostname = hostName;
		MQEnvironment.port = port;
		MQEnvironment.sslCipherSuite = sslCipherSuite;

		this.setCipherMappings();

		return new MQQueueManager(qmgrName);
	}

	/*
	 * Set the SSL Cipher Mapping rule depending on whether IBM or Oracle java is being used
	 */
	private final void setCipherMappings() {
		if (System.getProperty("com.ibm.mq.cfg.useIBMCipherMappings") == null) {
			if (System.getProperty("java.vendor").toLowerCase().indexOf("ibm") != -1) {
				System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "true");
			} else {
				System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
			}
		}
	}

	/**
	 * Set the SSL keystore details using the system environment variables. For this example, the keystore and truststore
	 * names are set to the same file.
	 * 
	 * @param jksFileName JKS file name used for the keystore and the truststore
	 * @param jksPassword Password to access the JSK file
	 */
	public final void setSSLKeystore(String jksFileName, String jksPassword) {
		if (jksFileName == null || jksPassword == null) {
			return;
		}

		System.setProperty("javax.net.ssl.trustStore", jksFileName);
		System.setProperty("javax.net.ssl.trustStorePassword", jksPassword);

		System.setProperty("javax.net.ssl.keyStore", jksFileName);
		System.setProperty("javax.net.ssl.keyStorePassword", jksPassword);
	}

}
