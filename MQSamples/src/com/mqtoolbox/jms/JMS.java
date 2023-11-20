package com.mqtoolbox.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.WMQConstants;

public class JMS {
	private MQConnectionFactory mqCF = null;

//	private QueueConnectionFactory factory = null;
//	private Queue outQueue = null;
	private Session session = null;
	private Connection connection = null;

	public JMS() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws JMSException {
		JMS pvt = new JMS();
//		if (pvt.bindConn("QMGR1") == false)
//			return;
		if (pvt.clientConn("QMGR1", "TEST.SVRCONN", "127.0.0.1", 1414) == false)
			return;
		pvt.createSessionToQmgr();
//		pvt.putJmsMsg("JMS.QL", "Hello there!!");
		pvt.putJmsMsg_WithoutMQRFH2("JMS.QL", "Hello there!!");
//		pvt.putJmsMsg2("JMS.QL", "Hello there!!", "REPLY.QL");
		pvt.getMsg("JMS.QL");
		pvt.close();
	}

	/**
	 * Create a direct bind connection to the queue manager
	 * 
	 * @param qmgrName Queue manager name
	 * @return
	 */
	public final boolean bindConn(String qmgrName) {
		mqCF = new MQConnectionFactory();
		try {
			mqCF.setQueueManager(qmgrName);

			// Establish a connection to the queue manager
			connection = mqCF.createConnection();
		} catch (JMSException jmsex) {
			return false;
		}
		return true;
	}

	/**
	 * Create a client connection to the queue manager
	 * 
	 * @param qmgrName    Queue manager name
	 * @param channelName SVRCONN channel name
	 * @param hostName    Host name or IP of the queue manager
	 * @param port        Queue manager listener port
	 * @return
	 */
	public final boolean clientConn(String qmgrName, String channelName, String hostName, int port) {
		mqCF = new MQConnectionFactory();
		try {
			mqCF.setTransportType(WMQConstants.WMQ_CM_CLIENT);
//			mqCF.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			mqCF.setQueueManager(qmgrName);
			mqCF.setHostName(hostName);
			mqCF.setPort(port);
			mqCF.setChannel(channelName);

			// Establish a connection to the queue manager
			connection = mqCF.createConnection();
		} catch (JMSException jmsex) {
			return false;
		}
		return true;
	}

	/**
	 * Start the session
	 * 
	 * @throws JMSException
	 */
	public final void createSessionToQmgr() throws JMSException {
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
	}

	/**
	 * Send a JMS message to the opened queue
	 * 
	 * @param queue  Opened queue
	 * @param msgStr Message to send/put
	 * @throws JMSException
	 */
	public final void putJmsMsg(String queueName, String msgStr) throws JMSException {
		Queue queue = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(queue); // Open the queue (to put messages to)
		TextMessage textMsg = session.createTextMessage(msgStr); // Create JMS text message
		producer.send(textMsg);
	}

	public final void putJmsMsg_WithoutMQRFH2(String queueName, String msgStr) throws JMSException {
		// New MQ 9.3 way of doing things
		// https://www.ibm.com/docs/en/ibm-mq/9.3?topic=applications-jms-jakarta-messaging-model

		// https://www.ibm.com/docs/en/ibm-mq/9.3?topic=messaging-mqconnectionfactory

		// Describes WMQ_MESSAGE_BODY_MQ but for a much older version of MQ 7.5. The
		// example below is different that this URL.
		// https://www.ibm.com/docs/en/ibm-mq/7.5?topic=features-accessing-websphere-mq-message-data
		// https://www.ibm.com/docs/en/ibm-mq/7.5?topic=data-destination-property-wmq-message-body

		// Gives a better example of setting WMQ_MESSAGE_BODY_MQ but the below code is a
		// little different
		// https://www.ibm.com/docs/en/ibm-mq/9.0?topic=package-using-mq-classes-jms

		JMSContext context = mqCF.createContext(); // JMS 2.0 - JMSContext combines Connection and Session
		Queue queue = context.createQueue(queueName); // Create a queue object
		JMSProducer producer = context.createProducer(); // Open the queue (to put messages to)
		((MQQueue) queue).setMessageBodyStyle(WMQConstants.WMQ_MESSAGE_BODY_MQ); // Remove the MQRFH2
		TextMessage textMsg = session.createTextMessage(msgStr); // Create JMS text message
		producer.send(queue, textMsg);
	}

	/**
	 * Send a JMS message to the opened queue
	 * 
	 * @param queue            Opened queue
	 * @param msgStr           Message to send/put
	 * @param replyToQueueName Send reply messages to this queue
	 * @throws JMSException
	 */
	public final void putJmsMsg2(String queueName, String msgStr, String replyToQueueName) throws JMSException {
		Queue queue = session.createQueue(queueName);

		MessageProducer producer = session.createProducer(queue); // Open the queue (to put messages to)
//		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); // Set the message persistency

		TextMessage textMsg = session.createTextMessage(); // Create JMS text message
		textMsg.setText(msgStr); // Set the message payload to this value
		textMsg.setJMSReplyTo((Destination) new MQQueue(replyToQueueName));
//		textMsg.setJMSCorrelationID("you can set");

		producer.send(textMsg); // Send the message
	}

	/**
	 * Get the next message on the queue. This does not match by correlation id.
	 * 
	 * @param queueName Get messages from this queue
	 * @throws JMSException
	 */
	public final void getMsg(String queueName) throws JMSException {
		Queue queue = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(queue); // Open the queue (to get messages from)
		TextMessage textMsg = (TextMessage) consumer.receive(10000); // Wait for a message
		System.out.println("Reply message:\n" + textMsg);
	}

	/**
	 * Close all the opened objects and connections
	 */
	public final void close() {
		if (session != null) {
			try {
				session.close();
			} catch (JMSException jmsex) {
				System.out.println("PVT: Session could not be closed.");
				return;
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException jmsex) {
				System.out.println("PVT: Connection could not be closed.");
				return;
			}
		}

	}
}
