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

import java.net.URL;
import java.net.MalformedURLException;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;

/**
 * Basic client connection to a queue manager using a Hashtable. This allows for SSL if required. Using a binary channel
 * TAB file as created by a queue manager when defining CLNTCONN channels. This feature was introduced in MQ 9.1.2 CD
 * and it is available in MQ 9.2 LTS and CD.
 *
 */
public class ClientConnection_HashtableUsingTAB_JSON {

	public static void main(String args[]) {
		ClientConnection_HashtableUsingTAB_JSON conn = new ClientConnection_HashtableUsingTAB_JSON();
		MQQueueManager qmgr1 = null;
		MQQueueManager qmgr2 = null;
		StopWatch stopwatch = new StopWatch();

		System.out.println("Client connection using hashtable with a JSON TAB file");
		try {
//			qmgr = conn.connect("QMGR1", "file:C:\\IBM\\MQ\\ProgramData\\qmgrs\\QMGR1\\@ipcc\\AMQCLCHL.TAB");
//			qmgr = conn.connect("QMGR1", "file:C:/mq/AMQCLCHL_QMGR1.JSON");

			// JSON file with a single queue manager name
//			qmgr = conn.connect("QMGR1", "file:AMQCLCHL_ONE_QMGR.TAB");

			// JSON file with multiple queue manager names
//			qmgr = conn.connect("QMGR1", "file:AMQCLCHL_MANY_QMGRS.JSON");
//			qmgr = conn.connect("QMGR2", "file:AMQCLCHL_MANY_QMGRS.JSON");

			// JSON file to connect to a queue manager group

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect 1"));
			qmgr1 = conn.connect("*QMGRGRP1", "file:AMQCLCHL_QMGR_GROUP.JSON");
			System.out.println(stopwatch.formatInProgressTimeTaken("After connect 1"));
			System.out.println("Connect to QMGR group " + qmgr1.getName() + " - " + qmgr1.getDescription());
			stopwatch.stop();

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect 2"));
			qmgr2 = conn.connect("*QMGRGRP1", "file:AMQCLCHL_QMGR_GROUP.JSON"); // Didn't code a disconnect
			System.out.println(stopwatch.formatInProgressTimeTaken("After connect 2"));
			System.out.println("Connect to QMGR group " + qmgr2.getName() + " - " + qmgr2.getDescription());
			stopwatch.stop();

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before disconnect 1"));
			qmgr1.disconnect();
			System.out.println(stopwatch.formatInProgressTimeTaken("After disconnect 1"));
			stopwatch.stop();

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before disconnect 2"));
			qmgr1.disconnect();
			System.out.println(stopwatch.formatInProgressTimeTaken("After disconnect 2"));
			stopwatch.stop();

		} catch (MQException e) {
			System.out.println(
					String.format("MQ error details: %s(%s)\n\n%s", MQConstants.lookupReasonCode(e.getReason()), e.getReason(), e.getCause()));
		} catch (MalformedURLException e) {
			System.out.println(String.format("MQ error details: Invalid CCDT URL\n\n%s", e.getMessage(), e.getCause()));
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
