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
 * This example creates a retained publication pub-sub scenario where:
 * - a publisher is running publishing to a topic
 * - after some time a subscriber appears
 * - the subscriber consumes retained publications
 * - the publisher then publishes a new publication at which time the subscriber consumes it
 * - run a few scenarios
 */
public class PubSubUsingRetainedPublications {

	private MQTopicConnectionFactory mqCF = null;
	private MQTopicConnection connection = null;
	private MQTopicSession session = null;

	public static void main(String[] args) {
		PubSubUsingRetainedPublications pvt = new PubSubUsingRetainedPublications();

		// if (pvt.bindConn("QMGR1") == false)
//		return;
		if (pvt.clientConn("QMGR1", "TEST.SVRCONN", "127.0.0.1", 1414) == false)
			return;

		MQTopicPublisher ibmStockPricePublisher = null;
		MQTopicPublisher oracleStockPricePublisher = null;
		MQTopicSubscriber ibmStockPriceSubscriber = null;
		MQTopicSubscriber oracleStockPriceSubscriber = null;
		MQTopicSubscriber allStockPriceSubscriber=null;
		
		Message msg = null;

		try {
			// Create a session to the queue manager
			pvt.createSessionToQmgr();

			// Create a connection to the topic
			MQTopic ibmStockPriceTopic = pvt.createTopic("topic://STOCK/PRICE/IBM");
			MQTopic oracleStockPriceTopic = pvt.createTopic("topic://STOCK/PRICE/ORACLE");

			// Create a publisher
			ibmStockPricePublisher = pvt.createPublisher(ibmStockPriceTopic);
			oracleStockPricePublisher = pvt.createPublisher(oracleStockPriceTopic);

			// Send a few publications
			Message ibmStockPriceMsg = null;
			Message oracleStockPriceMsg = null;

			// Retained publication 1
			ibmStockPriceMsg = pvt.getSession().createTextMessage("$120.00");
			ibmStockPriceMsg.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			ibmStockPricePublisher.send(ibmStockPriceMsg);

			oracleStockPriceMsg = pvt.getSession().createTextMessage("$75.00");
			oracleStockPriceMsg.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			oracleStockPricePublisher.send(oracleStockPriceMsg);

			// Retained publication 2, replaces retained publication 1
			ibmStockPriceMsg = pvt.getSession().createTextMessage("$135.00");
			ibmStockPriceMsg.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			ibmStockPricePublisher.send(ibmStockPriceMsg);

			oracleStockPriceMsg = pvt.getSession().createTextMessage("$98.00");
			oracleStockPriceMsg.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			oracleStockPricePublisher.send(oracleStockPriceMsg);
			
			// Retained publication 3, replaces retained publication 2
			ibmStockPriceMsg = pvt.getSession().createTextMessage("$127.54");
			ibmStockPriceMsg.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			ibmStockPricePublisher.send(ibmStockPriceMsg);

			// Create a subscriber
			ibmStockPriceSubscriber = pvt.createSubscriber(ibmStockPriceTopic);
			oracleStockPriceSubscriber = pvt.createSubscriber(oracleStockPriceTopic);

			// Now try to get publications; the most recent should be consumed
			msg = ibmStockPriceSubscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed IBM stock price " + msg);

			msg = oracleStockPriceSubscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed Oracle stock price " + msg);

			// Now consume all retained stock prices
			MQTopic allStockPriceTopic = pvt.createTopic("topic://STOCK/PRICE/#");
			allStockPriceSubscriber = pvt.createSubscriber(allStockPriceTopic);
			
			msg = allStockPriceSubscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed stock price " + msg);

			msg = allStockPriceSubscriber.receive(5000); // milliseconds
			if (msg == null)
				System.out.println("No publication");
			else
				System.out.println("Consumed stock price " + msg);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ibmStockPriceSubscriber != null) {
				try {
					ibmStockPriceSubscriber.close();
				} catch (JMSException jmsex) {
					System.out.println("PVT: Subscriber could not be closed.");
					return;
				}
			}
			if (oracleStockPriceSubscriber != null) {
				try {
					oracleStockPriceSubscriber.close();
				} catch (JMSException jmsex) {
					System.out.println("PVT: Publisher could not be closed.");
					return;
				}
			}
			if (ibmStockPricePublisher != null) {
				try {
					ibmStockPricePublisher.close();
				} catch (JMSException jmsex) {
					System.out.println("PVT: Publisher could not be closed.");
					return;
				}
			}
			if (oracleStockPricePublisher != null) {
				try {
					oracleStockPricePublisher.close();
				} catch (JMSException jmsex) {
					System.out.println("PVT: Publisher could not be closed.");
					return;
				}
			}
			if (allStockPriceSubscriber != null) {
				try {
					allStockPriceSubscriber.close();
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
