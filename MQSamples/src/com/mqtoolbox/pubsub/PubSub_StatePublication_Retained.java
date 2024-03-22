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
 * This example covers a 'state' publication. This is where a topic contains the current state of something. For
 * example, the current temperature. In this example the publication is set as a retained publication. This ensures when
 * a subscriber arrives it gets the current state of the topic and doesn't have to wait for a new publication to be put.
 */
public class PubSub_StatePublication_Retained {

	private MQTopicConnectionFactory mqCF = null;
	private MQTopicConnection connection = null;
	private MQTopicSession session = null;

	public static void main(String[] args) {
		PubSub_StatePublication_Retained pvt = new PubSub_StatePublication_Retained();

//		if (pvt.bindConn("QMGR1") == false)
//			return;
		if (pvt.clientConn("QMGR1", "TEST.SVRCONN", "127.0.0.1", 1414) == false)
			return;

		MQTopicPublisher publisher = null;
		MQTopicSubscriber subscriber = null;
		Message msg = null;

		try {
			// Create a session to the queue manager
			pvt.createSessionToQmgr();

			// Create a connection to the topic
			MQTopic topic = pvt.createTopic("topic://CURRENT_TEMPERATURE");

			// Create a publisher but create no subscriber at this time. This is to highlight that multiple retained publications
			// can be put and when the subscriber arrives, it gets the latest publication only.
			publisher = pvt.createPublisher(topic);

			Message statePublication_CurrentTemperature = null;

			// Put a retained publication
			statePublication_CurrentTemperature = pvt.getSession().createTextMessage("25C");
			statePublication_CurrentTemperature.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			publisher.send(statePublication_CurrentTemperature);

			// Put a retained publication
			statePublication_CurrentTemperature = pvt.getSession().createTextMessage("27C");
			statePublication_CurrentTemperature.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			publisher.send(statePublication_CurrentTemperature);

			// Put a retained publication
			statePublication_CurrentTemperature = pvt.getSession().createTextMessage("33C");
			statePublication_CurrentTemperature.setIntProperty(WMQConstants.JMS_IBM_RETAIN, WMQConstants.RETAIN_PUBLICATION);
			publisher.send(statePublication_CurrentTemperature);

			// Now get the latest retained publication
			subscriber = pvt.createSubscriber(topic);
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
