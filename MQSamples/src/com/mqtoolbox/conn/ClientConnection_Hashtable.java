package com.mqtoolbox.conn;

import java.util.Hashtable;

import javax.net.ssl.SSLSocketFactory;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.mqtoolbox.support.StopWatch;

/**
 * Basic client connection to a queue manager using a Hashtable. This allows for SSL if required.
 *
 */
public class ClientConnection_Hashtable {

//	DEFINE CHANNEL('TEST.SVRCONN') CHLTYPE(SVRCONN) -m qmgr3 CERTLABL('') COMPHDR(NONE) COMPMSG(NONE) DESCR('') DISCINT(0) HBINT(300) KAINT(AUTO) MAXINST(999999999) MAXINSTC(999999999) MAXMSGL(4194304) MCAUSER('noaccess') MONCHL(QMGR) RCVDATA('') RCVEXIT('') SCYDATA('') SCYEXIT('') SENDDATA('') SENDEXIT('') SHARECNV(10) SSLCAUTH(REQUIRED) SSLCIPH('') SSLPEER('') TRPTYPE(TCP)
//	SET CHLAUTH ('TEST.*') TYPE(USERMAP) DESCR('Allow') CUSTOM('') ADDRESS('') CLNTUSER('bdsae') MCAUSER('MUSR_MQADMIN') USERSRC(MAP) CHCKCLNT(ASQMGR) -m qmgr3
//	SET CHLAUTH ('TEST.*') TYPE(BLOCKUSER) DESCR('Default block') CUSTOM('') USERLIST(noaccess) WARN(NO) -m qmgr3

	public static void main(String args[]) {
		ClientConnection_Hashtable conn = new ClientConnection_Hashtable();
		MQQueueManager qmgr = null;
		StopWatch stopwatch = new StopWatch();

		System.out.println("Client connection using hashtable");
		try {
			stopwatch.start();
			System.out.println(stopwatch.formatInProgressTimeTaken("Before connect"));
//			qmgr = conn.connect("QMGR1", "TEST.SVRCONN", "localhost", 1414);
			qmgr = conn.connect("QMGR2", "TEST.SVRCONN", "localhost", 1415);
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
		@SuppressWarnings("rawtypes")
		Hashtable props = new Hashtable<String, SSLSocketFactory>();
		props.put(MQConstants.USER_ID_PROPERTY, System.getProperty("user.name"));
		props.put("channel", channelName);
		props.put("hostname", hostName);
		props.put("port", port);

		return new MQQueueManager(qmgrName, props);
	}
}
