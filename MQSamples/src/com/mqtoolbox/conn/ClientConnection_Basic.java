package com.mqtoolbox.conn;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;

/**
 * Basic client connection to a queue manager. Using MQEnvironment variables, while simple, is perhaps not the ideal way.
 *
 */
public class ClientConnection_Basic {

	public static void main(String args[]) {
		ClientConnection_Basic conn = new ClientConnection_Basic();
		MQQueueManager qmgr = null;
		StopWatch stopwatch = new StopWatch();
		
		System.out.println("Basic client connection");
		try {
			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect"));
			qmgr = conn.connect("QMGR1", "TEST.SVRCONN", "localhost", 1414);
			System.out.println(stopwatch.formatInProgressTimeTaken("After connect"));
			stopwatch.stop();

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before disconnect"));
			qmgr.disconnect();
			System.out.println(stopwatch.formatInProgressTimeTaken("After disconnect"));
			stopwatch.stop();
		} catch (MQException e) {
			System.out.println(String.format("MQ error details: %s(%s)", MQConstants.lookupReasonCode(e.getReason()), e.getReason()));
		}
	}

	public final MQQueueManager connect(String qmgrName, String channelName, String hostName, int port) throws MQException {
		MQEnvironment.userID = System.getProperty("user.name");
		MQEnvironment.channel = channelName;
		MQEnvironment.hostname = hostName;
		MQEnvironment.port = port;
		return new MQQueueManager(qmgrName);
	}
}
