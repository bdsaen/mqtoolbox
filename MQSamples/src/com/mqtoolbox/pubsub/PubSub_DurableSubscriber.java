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
package com.mqtoolbox.pubsub;

import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.Session;

import com.ibm.mq.jms.MQTopic;
import com.ibm.mq.jms.MQTopicConnection;
import com.ibm.mq.jms.MQTopicConnectionFactory;
import com.ibm.mq.jms.MQTopicPublisher;
import com.ibm.mq.jms.MQTopicSession;
import com.ibm.mq.jms.MQTopicSubscriber;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * This example covers an 'event' publication using a durable subscriber. This is where a subscriber requests all
 * publications to be kept (i.e. durable) until a time it is ready to consume them. This also caters for failure
 * scenarios where the subscriber stops, or closes the connection, and when it returns, it needs to consume all the events
 * generated subsequently.
 */
public class PubSub_DurableSubscriber {

	private MQTopicConnectionFactory mqCF = null;
	private MQTopicConnection connection = null;
	private MQTopicSession session = null;

	public static void main(String[] args) {
		PubSub_DurableSubscriber pvt = new PubSub_DurableSubscriber();

//		if (pvt.bindConn("QMGR1") == false)
//			return;
		if (pvt.clientConn("QMGR1", "TEST.SVRCONN", "127.0.0.1", 1414, "aUniqueClientId") == false)
			return;

		MQTopicPublisher publisher = null;
		MQTopicSubscriber subscriber = null;
		Message msg = null;

		try {
			// Create a session to the queue manager
			pvt.createSessionToQmgr();

			// Create a connection to the topic
			MQTopic topic = pvt.createTopic("topic://PROPERTY_SOLD");

			// Create a durable subscriber. One option caters for a regular subscriber, the other a durable subscriber. Comment out
			// each one to test the difference. Be sure to adjust the similar code following.
//			subscriber = pvt.createSubscriber(topic);
			subscriber = pvt.createDurableSubscriber(topic, "MySubName");

			// Create a publisher but create no subscriber at this time.
			publisher = pvt.createPublisher(topic);

			Message eventPublication_PropertiesSold = null;

			// Put a publication
			eventPublication_PropertiesSold = pvt.getSession().createTextMessage("12 Bart Street Sydney");
			publisher.send(eventPublication_PropertiesSold);

			// Put a publication
			eventPublication_PropertiesSold = pvt.getSession().createTextMessage("17 Higinbotham road Rome");
			publisher.send(eventPublication_PropertiesSold);

			// Put a publication
			eventPublication_PropertiesSold = pvt.getSession().createTextMessage("99 Charming road Westchester");
			publisher.send(eventPublication_PropertiesSold);

			// Now get the latest publication.
			while (true) {
				msg = subscriber.receive(5000); // milliseconds
				if (msg == null) {
					System.out.println("No publication");
					break;
				} else
					System.out.println("Consumed " + msg);
			}

			// Close the subscribe
			subscriber.close();

			// Put a publication.
			eventPublication_PropertiesSold = pvt.getSession().createTextMessage("1012 State road Townsville");
			publisher.send(eventPublication_PropertiesSold);

			eventPublication_PropertiesSold = pvt.getSession().createTextMessage("99 Cashew Lane Oxford");
			publisher.send(eventPublication_PropertiesSold);

			// Re-subscribe. Comment out the relevant code.
//			subscriber = pvt.createSubscriber(topic);
			subscriber = pvt.createDurableSubscriber(topic, "MySubName");

			// Now get the latest publication. As the subscriber was created before the publication was put, it will get a
			// publication.
			while (true) {
				msg = subscriber.receive(5000); // milliseconds
				if (msg == null) {
					System.out.println("No publication");
					break;
				} else
					System.out.println("Consumed " + msg);
			}

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

	public final void sean() {

	}

	/**
	 * Create a durable subscriber to the topic
	 * 
	 * @param topic Topic name
	 * @param name  Name of the durable subscription
	 * @return
	 * @throws JMSException
	 */
	public final MQTopicSubscriber createDurableSubscriber(MQTopic topic, String name) throws JMSException {
		return (MQTopicSubscriber) session.createDurableSubscriber(topic, name);
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
	 * @param clientID    Client ID - this is needed for durable subscribers
	 * @return
	 */
	public final boolean clientConn(String qmgrName, String channelName, String hostName, int port, String clientID) {
		mqCF = new MQTopicConnectionFactory();
		try {
			mqCF.setTransportType(WMQConstants.WMQ_CM_CLIENT);
			mqCF.setQueueManager(qmgrName);
			mqCF.setHostName(hostName);
			mqCF.setPort(port);
			mqCF.setChannel(channelName);
			mqCF.setClientID(clientID);

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
