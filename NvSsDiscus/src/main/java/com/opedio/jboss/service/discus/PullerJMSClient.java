package com.opedio.jboss.service.discus;

import javax.jms.ObjectMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Queue;
import javax.jms.MessageProducer;
import javax.jms.JMSException;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.Context;

import org.jboss.logging.Logger;
import com.solusi247.poin.data.WFSendSmsQueue;

public class PullerJMSClient extends Thread {

	private static Logger logger = Logger.getLogger(PullerJMSClient.class);
	private static AtomicInteger _id_gen = new AtomicInteger();

	private String JMS_CONN_FACTORY = "java:/ConnectionFactory";
	private String JMS_QUEUE = "queue/WFSendSmsQueue";
	
	private QueueConnection queueConnection = null;
	private QueueSession queueSession       = null;
	private Queue queue                     = null;
	private MessageProducer sender = null;
	private String name;
	private boolean keep_running = true;
	
	private BlockingQueue<WFSendSmsQueue> _sms_queue = null;
	
	public PullerJMSClient(String jndiQueue, BlockingQueue<WFSendSmsQueue> sms_queue) {
		this.JMS_QUEUE = jndiQueue;
		this._sms_queue = sms_queue;
		this.name = "Puller-JMS-client-"+  _id_gen.getAndIncrement();
		this.setName(this.name);
		//_broker.add(this);
	}
		

	public void ophouden() {
		this.keep_running = false;
	}
	
	public String getJmsQueuename() {
		return this.JMS_QUEUE;
	}

	

	private void openJmsConnection() { // throws Exception {
		
		logger.info("[" + name +" "+ JMS_QUEUE +" ] startApp");
		
		try {
			Context context = new InitialContext();
			queue = (Queue) context.lookup(JMS_QUEUE);

			QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(JMS_CONN_FACTORY);
			
			queueConnection = factory.createQueueConnection();
			
			queueSession = queueConnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
			
			//queueSession = queueConnection.createQueueSession(false, QueueSession.CLIENT_ACKNOWLEDGE);
			// Session.DUPS_OK_ACKNOWLEDGE);
			//queueSession = queueConnection.createQueueSession(false, QueueSession.DUPS_OK_ACKNOWLEDGE);
			
			
			sender = queueSession.createProducer(queue);
			
			sender.setDisableMessageID(true);
			sender.setDisableMessageTimestamp(true);
			//  queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
		} catch (JMSException e) {
			logger.error("[startApp] ("+ JMS_QUEUE +"), " + e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}
		} catch (Exception e) {
			logger.error("[startApp] ("+ JMS_QUEUE +"), " + e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}

		}

		logger.info("[" + name +" "+ JMS_QUEUE +" ] started");
	}


	private void closeJmsConnection() { //throws Exception {
		
		logger.info("[" + name +" "+ JMS_QUEUE +" ] stopApp");
		
		try {
			sender.close();

		} catch (Exception e) {
			logger.error("[" + name +"] ("+ JMS_QUEUE +"),  " + e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}		
		}

		try {
			queueSession.close();

		} catch (Exception e) {
			logger.error("[" + name +"] ("+ JMS_QUEUE +"), " + e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}		
		}
		try {
			queueConnection.close();

		} catch (JMSException e) {
			logger.error("[" + name +"] ("+ JMS_QUEUE +"), " + e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}		
		} catch (Exception e) {
			logger.error("[" + name +"] ("+ JMS_QUEUE +"), " + e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}		
		}
		
		logger.info("[" + name +" "+ JMS_QUEUE +" ] stopped");
	}

	
	@Override
	public void run() {
		openJmsConnection();
		
		
		while (keep_running || !_sms_queue.isEmpty()) {
			try {
				//WFSendSmsQueue m = _sms_queue.take();
				WFSendSmsQueue sms = _sms_queue.poll(1000, TimeUnit.MILLISECONDS);
				
				if (sms!=null) {
					ObjectMessage msg = queueSession.createObjectMessage(sms);
					
					//logger.debug(sms.get_msgid() +"|"+ sms.get_enqueue_uid() +"|"+ sms.get_msisdn() +"|"+ sms.get_msgtxt());

					// send the message
					sender.send(msg);
				}
			} catch (JMSException e) {
				logger.error("["+ name +"] "+ JMS_QUEUE +" " + e);
				for (StackTraceElement s: e.getStackTrace()) {
					logger.error(" at "+ s);
				}
			} catch (InterruptedException e) {
				logger.warn("[" + name +"] " + e); /*
				for (StackTraceElement s: e.getStackTrace()) {
					logger.error(" at "+ s);
				} */

			} catch (Exception e) {
				logger.error("[" + name +"] " + e);
				for (StackTraceElement s: e.getStackTrace()) {
					logger.error(" at "+ s);
				}

			}
		}

		
		
		closeJmsConnection();
	}

}
