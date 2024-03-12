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

import java.net.MalformedURLException;
import java.net.URL;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

/**
 * Basic client connection to a queue manager using a Hashtable. This allows for SSL if required. Using a binary channel
 * TAB file as created by a queue manager when defining CLNTCONN channels
 *
 */
public class ClientConnection_HashtableUsingTAB_Binary {

	public static void main(String args[]) {
		ClientConnection_HashtableUsingTAB_Binary conn = new ClientConnection_HashtableUsingTAB_Binary();
		MQQueueManager qmgr = null;

		System.out.println("Client connection using hashtable with a binary TAB file");
		try {
//			qmgr = conn.connect("QMGR1", "file:C:\\IBM\\MQ\\ProgramData\\qmgrs\\QMGR1\\@ipcc\\AMQCLCHL.TAB");
//			qmgr = conn.connect("QMGR1", "file:C:/IBM/MQ/ProgramData/qmgrs/QMGR1/@ipcc/AMQCLCHL.TAB");
			qmgr = conn.connect("QMGR1", "file:AMQCLCHL_ONE_QMGR.TAB");
		} catch (MQException e) {
			System.out.println(
					String.format("MQ error details: %s(%s)\n\n%s", MQConstants.lookupReasonCode(e.getReason()), e.getReason(), e.getCause()));
		} catch (MalformedURLException e) {
			System.out.println(String.format("MQ error details: Invalid CCDT URL\n\n%s", e.getMessage(), e.getCause()));
		} finally {
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
	 * Connect to the queue manager using a CCDT (client channel definition table)
	 * 
	 * @param qmgrName Queue manager to connect to
	 * @param ccdtStr  Client Channel Definition Table. Example, "file:///c:/mq/AMQCLCHL.TAB"
	 * @return
	 * @throws MQException
	 * @throws MalformedURLException
	 */
	public final MQQueueManager connect(String qmgrName, String ccdtStr) throws MQException, MalformedURLException {
		return new MQQueueManager(qmgrName, new URL(ccdtStr));
	}
}
