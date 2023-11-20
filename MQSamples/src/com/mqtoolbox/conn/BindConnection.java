package com.mqtoolbox.conn;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;

/**
 * Bind connection to a queue manager running on the same machine
 *
 */
public class BindConnection {

	public static void main(String args[]) {
		BindConnection conn = new BindConnection();
		MQQueueManager qmgr = null;
		StopWatch stopwatch = new StopWatch();
		
		System.out.println("Bind connection");
		try {
			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect"));
			qmgr = conn.connect("QMGR1");
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

	public final MQQueueManager connect(String qmgrName) throws MQException {
		return new MQQueueManager(qmgrName);
	}
}
