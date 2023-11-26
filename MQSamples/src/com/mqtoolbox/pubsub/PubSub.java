package com.mqtoolbox.pubsub;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.ibm.mq.jms.MQTopic;
import com.ibm.mq.jms.MQTopicConnection;
import com.ibm.mq.jms.MQTopicConnectionFactory;
import com.ibm.mq.jms.MQTopicPublisher;
import com.ibm.mq.jms.MQTopicSession;
import com.ibm.mq.jms.MQTopicSubscriber;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * This example creates a basic pub-sub scenario where:
 * - a publisher is running publishing to a topic
 * - after some time a subscriber appears
 * - as there are no retained publications the subscriber consumes no publications
 * - the publisher then publishes a new publication at which time the subscriber consumes it
 * - run a few scenarios
 */
public class PubSub {

	private MQTopicConnectionFactory mqCF = null;
	private MQTopicConnection connection = null;
	private MQTopicSession session = null;

	public static void main(String[] args) {
		PubSub pvt = new PubSub();

		// if (pvt.bindConn("QMGR1") == false)
//		return;
		if (pvt.clientConn("QMGR1", "TEST.SVRCONN", "127.0.0.1", 1414) == false)
			return;

		MQTopicPublisher publisher = null;
		MQTopicSubscriber subscriber = null;
		Message msg = null;

		try {
			// Create a session to the queue manager
			pvt.createSessionToQmgr();

			// Create a connection to the topic
			MQTopic topic = pvt.createTopic("topic://MARKET_OPEN");

			// Create a publisher
			publisher = pvt.createPublisher(topic);

			System.out.println("1 publisher, 0 subscribers, publish 3 publications, then subscribe, consume 0 publications");
			
			// Send a few publications
			publisher.send(pvt.getSession().createTextMessage("Market is open on 1 Jan 2024"));
			publisher.send(pvt.getSession().createTextMessage("Market is closed on 1 Jan 2024"));
			publisher.send(pvt.getSession().createTextMessage("Market is open on 2 Jan 2024"));

			// Create a subscriber
			subscriber = pvt.createSubscriber(topic);

			// Now try to get publications; there should be no messages as none of the publications were set as retained
			msg = subscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed " + msg);

			// We now have 1 publisher and 1 subscriber
			// Publish 1 publication and consume it
			System.out.println("1 publisher, 1 subscribers, publish 1 publication, then consume 1 publication");

			publisher.send(pvt.getSession().createTextMessage("Market is closed on 2 Jan 2024"));
			msg = subscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed " + msg);
			msg = subscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed " + msg);

			// We now have 1 publisher and 1 subscriber
			// Publish 2 publication and consume them both
			System.out.println("1 publisher, 1 subscriber, publish 2 publications, then consume 2 publications");

			publisher.send(pvt.getSession().createTextMessage("Market is open on 3 Jan 2024"));
			publisher.send(pvt.getSession().createTextMessage("Market is closed on 3 Jan 2024"));
			msg = subscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed " + msg);
			msg = subscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed " + msg);
			msg = subscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed " + msg);

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (subscriber != null) {
				try {
					subscriber.close();
				} catch (JMSException jmsex) {
					System.out.println("PVT: Subscriber could not be closed.");
					return;
				}
			}
			if (publisher != null) {
				try {
					publisher.close();
				} catch (JMSException jmsex) {
					System.out.println("PVT: Publisher could not be closed.");
					return;
				}
			}

			pvt.close();
		}
	}

	/**
	 * Start the session
	 * 
	 * @throws JMSException
	 */
	public final void createSessionToQmgr() throws JMSException {
		session = (MQTopicSession) connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
	}

	/**
	 * Create a connection to the topic
	 * 
	 * @param topicStr Topic string to connect to
	 * @return
	 * @throws JMSException
	 */
	public final MQTopic createTopic(String topicStr) throws JMSException {
		return (MQTopic) session.createTopic(topicStr);
	}

	/**
	 * Create a publisher to the topic
	 * 
	 * @param topic
	 * @return
	 * @throws JMSException
	 */
	public final MQTopicPublisher createPublisher(MQTopic topic) throws JMSException {
		return (MQTopicPublisher) session.createPublisher(topic);
	}

	/**
	 * Create a subscriber to the topic
	 * 
	 * @param topic
	 * @return
	 * @throws JMSException
	 */
	public final MQTopicSubscriber createSubscriber(MQTopic topic) throws JMSException {
		return (MQTopicSubscriber) session.createSubscriber(topic);
	}

	/**
	 * Create a direct bind connection to the queue manager
	 * 
	 * @param qmgrName Queue manager name
	 * @return
	 */
	public final boolean bindConn(String qmgrName) {
		mqCF = new MQTopicConnectionFactory();
		try {
			mqCF.setQueueManager(qmgrName);

			// Establish a connection to the queue manager
			connection = (MQTopicConnection) mqCF.createTopicConnection();
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
		mqCF = new MQTopicConnectionFactory();
		try {
			mqCF.setTransportType(WMQConstants.WMQ_CM_CLIENT);
//			mqCF.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			mqCF.setQueueManager(qmgrName);
			mqCF.setHostName(hostName);
			mqCF.setPort(port);
			mqCF.setChannel(channelName);

			// Establish a connection to the queue manager
			connection = (MQTopicConnection) mqCF.createTopicConnection();
		} catch (JMSException jmsex) {
			return false;
		}
		return true;
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

	/**
	 * @return the session
	 */
	public MQTopicSession getSession() {
		return session;
	}
}
