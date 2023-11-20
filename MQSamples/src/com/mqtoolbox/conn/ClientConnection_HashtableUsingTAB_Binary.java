package com.mqtoolbox.conn;

import java.net.URL;
import java.net.MalformedURLException;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;

/**
 * Basic client connection to a queue manager using a Hashtable. This allows for SSL if required. Using a binary channel TAB file as created
 * by a queue manager when defining CLNTCONN channels
 *
 */
public class ClientConnection_HashtableUsingTAB_Binary {

//	DEFINE CHANNEL('TEST.SVRCONN') CHLTYPE(SVRCONN) -m qmgr3 CERTLABL('') COMPHDR(NONE) COMPMSG(NONE) DESCR('') DISCINT(0) HBINT(300) KAINT(AUTO) MAXINST(999999999) MAXINSTC(999999999) MAXMSGL(4194304) MCAUSER('noaccess') MONCHL(QMGR) RCVDATA('') RCVEXIT('') SCYDATA('') SCYEXIT('') SENDDATA('') SENDEXIT('') SHARECNV(10) SSLCAUTH(REQUIRED) SSLCIPH('') SSLPEER('') TRPTYPE(TCP)
//	SET CHLAUTH ('TEST.*') TYPE(USERMAP) DESCR('Allow') CUSTOM('') ADDRESS('') CLNTUSER('bdsae') MCAUSER('MUSR_MQADMIN') USERSRC(MAP) CHCKCLNT(ASQMGR) -m qmgr3
//	SET CHLAUTH ('TEST.*') TYPE(BLOCKUSER) DESCR('Default block') CUSTOM('') USERLIST(noaccess) WARN(NO) -m qmgr3

	public static void main(String args[]) {
		ClientConnection_HashtableUsingTAB_Binary conn = new ClientConnection_HashtableUsingTAB_Binary();
		MQQueueManager qmgr = null;
		StopWatch stopwatch = new StopWatch();

		System.out.println("Client connection using hashtable with a binary TAB file");
		try {
			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect"));
//			qmgr = conn.connect("QMGR1", "file:C:\\IBM\\MQ\\ProgramData\\qmgrs\\QMGR1\\@ipcc\\AMQCLCHL.TAB");
//			qmgr = conn.connect("QMGR1", "file:C:/IBM/MQ/ProgramData/qmgrs/QMGR1/@ipcc/AMQCLCHL.TAB");
			qmgr = conn.connect("QMGR1", "file:AMQCLCHL_QMGR1.TAB");
			System.out.println(stopwatch.formatInProgressTimeTaken("After connect"));
			stopwatch.stop();

			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before disconnect"));
			qmgr.disconnect();
			System.out.println(stopwatch.formatInProgressTimeTaken("After disconnect"));
			stopwatch.stop();
		} catch (MQException e) {
			System.out.println(String.format("MQ error details: %s(%s)", MQConstants.lookupReasonCode(e.getReason()), e.getReason()));
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println(String.format("MQ error details: Invalid CCDT URL", e.getMessage()));
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
