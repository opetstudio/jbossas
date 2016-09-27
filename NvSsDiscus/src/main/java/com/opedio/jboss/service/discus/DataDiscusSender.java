package com.opedio.jboss.service.discus;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.solusi247.poin.data.WFSendSmsQueue;
import com.solusi247.poin.util.TselpoinIDGenerator;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.sql.DataSource;
import javax.tools.JavaFileObject;


public class DataDiscusSender extends Thread {

	private static Logger logger = Logger.getLogger(DataDiscusSender.class);
	private boolean keep_running = false;
/*	     <jms-queue name="WFSendSmsQueue">
                        <entry name="queue/WFSendSmsQueue"/>
                        <entry name="java:jboss/exported/jms/queue/WFSendSmsQueue"/>
         </jms-queue>
		 <jms-queue name="WFSendSmsHisQueue">
                        <entry name="queue/WFSendSmsHisQueue"/>
                        <entry name="java:jboss/exported/jms/queue/WFSendSmsHisQueue"/>
         </jms-queue> */
	private List<PullerJMSClient>_broker = new ArrayList<PullerJMSClient>();
	private int _nr_of_jms_connections = 4;
	private String JMS_QUEUE = "queue/WFSendSmsQueue";
	private BlockingQueue<WFSendSmsQueue> _sms_queue = null;
	private int _max_row_num = 1000;
	private int _timer = 60000;
	private DataSource ds = null;
	private DB db = null;
	
	 private volatile long start_time;//ms
	    private volatile long end_time;
	    private volatile long dif_time;
	
	public DataDiscusSender(int thread_number, int queue_size, int row_num, int timer, DataSource datasource, DB mongoDB) {
		if (thread_number>0)
			this._nr_of_jms_connections = thread_number;
		
		if (queue_size>=(2*_nr_of_jms_connections))
			_sms_queue = new ArrayBlockingQueue<WFSendSmsQueue>(queue_size);
		else _sms_queue = new ArrayBlockingQueue<WFSendSmsQueue>(3*this._nr_of_jms_connections);
		if (row_num>0)
			this._max_row_num = row_num;
		if (timer>100)
			this._timer = timer;
		this.ds = datasource;
		this.db = mongoDB;
		logger.info("construct SmsSender");
	}
	
	public void ophouden() {
		this.keep_running = false;
	}
	
	private void startApp() {
		logger.info("startApp");
		// TODO
		
		/*for (int i=0;i<_nr_of_jms_connections;i++) {
			
			PullerJMSClient jj = new PullerJMSClient(JMS_QUEUE,_sms_queue);
			jj.start();
			
			_broker.add(jj);
		}*/
	}
	
	private void stopApp() {
		logger.info("[SmsSender] is stopping");
		
		for (PullerJMSClient j: _broker) {	
			try {
				j.ophouden();
				j.interrupt();
			} catch (Exception e) {
				logger.error("[closeJMSConnection] "+  e);
				for (StackTraceElement s: e.getStackTrace()) {
					logger.error(" at "+ s);
				}
			}
		}
		
		
		for (PullerJMSClient j: _broker) {	
			try {
				if (j!=null && j.isAlive()) {
					j.join();
				}

			} catch (Exception e) {
				logger.error("[closeJMSConnection] "+  e);
				for (StackTraceElement s: e.getStackTrace()) {
					logger.error(" at "+ s);
				}
			}
		}
		
		logger.info("[SmsSender] closeJMSConnection done");
	}
	
	
	@Override
	public void run() {
		keep_running = true;
		startApp();

		while (keep_running) {
			try {
//				getData();
				getDataMongo();
//				logger.info("TIMER");
			} catch (Exception e) {
				logger.error(e);
//				keep_running = false;
			}
			
			try {
				Thread.sleep(_timer);
			} catch (InterruptedException e) {
				keep_running = false;
				logger.error(e);
			}
			
		}

		stopApp();

		logger.info(" : Done");
	}
	

	private void getDataMongo(){
		//mongoDB
		logger.info("getDataMongo");
		
		
		
		JSONArray All_ja = null;
		
		DBCollection coll = db.getCollection("WFSSDiscusMsgCollection");
		DBCursor cursor = coll.find();
//		int i = 0;
		try {
		   while(cursor.hasNext()) {
//		       System.out.println(cursor.next());
		       //array object
			   try {
//				   logger.info(i+". "+cursor.next().get("data"));
//				   i++;
				   	if(!JSONUtils.isValid(""+cursor.next().get("data"))){
						logger.error("colom data bukan json string: "+cursor.next().get("data").toString());
					}
				   else{
					   //merge string
					   try {
						   
						   JSONParser parser = new JSONParser();
						   Object obj = parser.parse(""+cursor.next().get("data"));
						   JSONArray ja = (JSONArray) obj;
						   
							logger.info("array length: "+ja.size());
//							logger.info("data: "+cursor.next().get("data").toString());
							for (int i = 0; i < ja.size(); i++) {
								logger.info(ja.get(i));
//							    destinationArray.put(sourceArray.getJSONObject(i));
							}
//							result.put("poin", ja);
						} catch (ParseException e1) {
//							e1.printStackTrace();
							logger.error(""+ e1.getMessage());
							for (StackTraceElement se: e1.getStackTrace()) {
								logger.error(" at "+ se);
							}
						}
				   }
				} catch (Exception e) {
					logger.error("colom data tidak ada. e:"+e.getMessage());
					for (StackTraceElement se: e.getStackTrace()) {
						logger.error(" at "+ se);
					}
				}
			   
			   
		       //delete row
		   }
		} finally {
		   cursor.close();
		}
		
		//HIT and send data to apps2
	}
	private void getData(){
		//protected String query = "select * from WF_QUEUE_SENDSMS where rownum < ";
		//protected String query1 = "delete WF_QUEUE_SENDSMS where MSGID = ?";
		
		JavaFileObject jObj = null;

		
		 String q_WF_QUEUE_SENDSMS = "select msgid, msisdn,msgtxt, delay, expiration,corrid, enq_uid, deq_time, " +
		 		"enq_time, priority, status, q_name, sender from WF_QUEUE_SENDSMS where rownum <" + _max_row_num;

		 String del_WF_QUEUE_SENDSMS = "delete WF_QUEUE_SENDSMS where MSGID = ?";
		 
		
		java.sql.Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		Vector<Long> sms_sents = new Vector<Long>();
//		logger.info("GET DATA FROM NODE JS");
		String _line_ = "";
//		_line_ = "https://192.168.137.1:8000/puller";
//		_line_ = "http://mmm2-opedio.rhcloud.com/puller";
		_line_ = "http://oksipyoucansmsboxto9116-as10009116.rhcloud.com/puller?uname=jboss&pass=jboss123";
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
		CloseableHttpResponse   response1 = null;
		try {
		    //	httpGet = new HttpGet("http://targethost/homepage");
			logger.debug("HIT "+_line_);
			httpGet = new HttpGet(_line_);
			
			 start_time = System.currentTimeMillis();
//			 logger.info(" HIT "+_line_);
//			 logger.debug(" HIT  start_time="+start_time+" url:"+_line_);
			 response1 = httpclient.execute(httpGet);
			 
				 HttpEntity entity1 = response1.getEntity();
				 String responseString = EntityUtils.toString(entity1, "UTF-8");
				 end_time = System.currentTimeMillis();
				 dif_time = end_time - start_time;
				 float elapse = (end_time - start_time) / 1000f;
			 
//		    logger.debug(" start_time="+start_time+" end_time="+end_time+" dif="+dif_time+" elapse="+elapse+" resp = "+ response1.getStatusLine()+" json="+responseString);
//		    JSONObject obj = new JSONObject();
		    JSONParser parser=new JSONParser();
		    Object obj = parser.parse(responseString);
		    JSONArray array = (JSONArray)obj;
		    
		    for (int i = 0; i < array.toArray().length; i++) { 
		    	JSONObject obj2 = (JSONObject)array.get(i);
		    	logger.debug("msgtxt: "+obj2.get("MSGTXT"));
		    	logger.debug("msisdn: "+obj2.get("MSISDN"));
		    	
		    	long _msg_id = TselpoinIDGenerator.getNextTselpoinID();
		    	
		    	WFSendSmsQueue sms = new WFSendSmsQueue();
				
//				sms.set_msgid(Long.parseLong(""+obj2.get("MSGID")));
				sms.set_msgid(_msg_id);
				sms.set_msisdn(""+obj2.get("MSISDN"));
				sms.set_msgtxt(""+obj2.get("MSGTXT"));
				
//				sms.set_delay(rs.getInt(4));
//				sms.set_expiration(rs.getInt(5));
//				sms.set_corrid(rs.getString(6));
				
//				sms.set_enqueue_uid(rs.getInt(7));
//				sms.set_dequeue_time(rs.getDate(8));
//				sms.set_enqueue_time(rs.getDate(9));
				
//				sms.set_priority(rs.getInt(10));
				sms.set_status(""+obj2.get("STATUS"));
				sms.set_q_name(""+obj2.get("Q_NAME"));
//				sms.set_sender("777");
				logger.debug("send to queue");
				_sms_queue.put(sms);
				
//				sms_sents.add(Long.parseLong(""+obj2.get("MSGID")));
		    }
//		    JSONObject jsonObj = new JSONObject(responseString);
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    
		    
		    EntityUtils.consume(entity1);
		    
		    
		} catch (ClientProtocolException e) {
		    /*
		    Multiple markers at this line
			- Unhandled exception type ClientProtocolException
			- Unhandled exception type IOException
			- Resource leak: 'response1' is not closed at this 
			 location
		    */
			logger.error("ClientProtocolException, "+ e);
//			for (StackTraceElement s: e.getStackTrace()) {
//				logger.error(" at "+ s);
//			}
		} catch (IOException e) {
			logger.error("IOException, "+ e);
//			for (StackTraceElement s: e.getStackTrace()) {
//				logger.error(" at "+ s);
//			}
		} catch (Exception e) {
			logger.error("Exception, "+ e);
//			for (StackTraceElement s: e.getStackTrace()) {
//				logger.error(" at "+ s);
//			}
		} finally {
			try { response1.close(); } catch (IOException e) {}
		}
		
		
//		processing(null);
		
//		try {
//
//			conn = ds.getConnection();
//			st = conn.createStatement();
//
//			rs = st.executeQuery(q_WF_QUEUE_SENDSMS); //gjhgjg
//
//			while (rs.next()) {
//				
//				WFSendSmsQueue sms = new WFSendSmsQueue();
//				
//				sms.set_msgid(rs.getInt(1));
//				sms.set_msisdn(rs.getString(2));
//				sms.set_msgtxt(rs.getString(3));
//				
//				sms.set_delay(rs.getInt(4));
//				sms.set_expiration(rs.getInt(5));
//				sms.set_corrid(rs.getString(6));
//				
//				sms.set_enqueue_uid(rs.getInt(7));
//				sms.set_dequeue_time(rs.getDate(8));
//				sms.set_enqueue_time(rs.getDate(9));
//				
//				sms.set_priority(rs.getInt(10));
//				sms.set_status(rs.getString(11));
//				sms.set_q_name(rs.getString(12));
//				sms.set_sender(rs.getString(13));
//					
//				_sms_queue.put(sms);
//				
//				sms_sents.add(sms.get_msgid());
//
//			}
//			
//			ps = conn.prepareStatement(del_WF_QUEUE_SENDSMS);
//			
//			for (long msg_id: sms_sents) {
//				
//				ps.setLong(1,msg_id);
//				
//				ps.executeUpdate();
//
//			}
//				
//			
//			
//		} finally {
//			if (st != null){ try {st.close();}	catch (SQLException e){}	}
//			if (ps != null){ try {ps.close();}	catch(SQLException e){}	}
//			if (conn!=null){ try{conn.close();} catch (SQLException e) {}
//			}
//		}
		
	}
	private void processing(WFSendSmsQueue _send_message){
		String _line_ = "";
		//CallbackCDDS m = _queue.take(); // head or waiting
		WFSendSmsQueue m = _send_message;
		
		
		String adn_from = "777";
		//UNTUK SMAULOOP
    	try {
    		if(m.get_q_name().equals("SMAULOOP")){
				adn_from = "2323";
			}else{
				adn_from = "777";
			}
		} catch (Exception e) {
			adn_from = "777";
		}
    	
    	//text message
    	String textmsg = "";
    	try {
    		textmsg = URLEncoder.encode(m.get_msgtxt(),"UTF-8");
		} catch (Exception e) {
			textmsg = m.get_msgtxt();
		}
    	String qName = "";
    	try {
    		qName = m.get_q_name();
        	if(m.get_q_name().equals(null)){
        		qName = "DEFAULT";
        	}
		} catch (Exception e) {
			qName = "DEFAULT";
		}
    	
    	
    	
    	_line_ = "http://192.168.137.1:3000/puller";
//    	_line_ = this._base_url +"?user="+this._user+"&pass="+this._pwd+"&from="+adn_from+"&to=" + m.get_msisdn() +"&text="+textmsg+"&qname="+qName;
//    	String url_line = this._base_url+"___&from="+adn_from+"&to=" + m.get_msisdn() +"&text="+textmsg+"&qname="+qName;
//    	_line_ = this._base_url +"?user=TPOIN&pass=mt2014&from=TPOIN&to="+ m.get_msisdn() +"&text=test";
    	
		
		// CloseableHttpClient httpclient = HttpClients.createDefault();
    			
    			CloseableHttpClient httpclient = HttpClients.createDefault();
		
				HttpGet httpGet = null;
				CloseableHttpResponse   response1 = null;
				
				// The underlying HTTP connection is still held by the response object
				// to allow the response content to be streamed directly from the network socket.
				// In order to ensure correct deallocation of system resources
				// the user MUST either fully consume the response content  or abort request
				// execution by calling CloseableHttpResponse#close().

				try {
				    //	httpGet = new HttpGet("http://targethost/homepage");
					httpGet = new HttpGet(_line_);
					
	    			 start_time = System.currentTimeMillis();
	    			 logger.debug(" HIT  start_time="+start_time+" msgid="+ m.get_msgid()  +"|" +m.get_msisdn() +"|enq="+m.get_enqueue_time() +"|deq=" +m.get_dequeue_time() +"|" +m.get_msgtxt()+"|adn:"+adn_from+" q:"+m.get_q_name()+" url:"+_line_);
					 response1 = httpclient.execute(httpGet);
					 
						 HttpEntity entity1 = response1.getEntity();
						 end_time = System.currentTimeMillis();
						 dif_time = end_time - start_time;
						 float elapse = (end_time - start_time) / 1000f;
					 
				    logger.debug("msgid = "+ m.get_msgid() +"msisdn="+m.get_msisdn()+" start_time="+start_time+" end_time="+end_time+" dif="+dif_time+" elapse="+elapse+" resp = "+ response1.getStatusLine());
					    
				    // do something useful with the response body
				    // and ensure it is fully consumed
				    
				    
				    EntityUtils.consume(entity1);
				    
				    
				} catch (ClientProtocolException e) {
				    /*
				    Multiple markers at this line
					- Unhandled exception type ClientProtocolException
					- Unhandled exception type IOException
					- Resource leak: 'response1' is not closed at this 
					 location
				    */
					logger.error("ClientProtocolException, "+ e);
//					for (StackTraceElement s: e.getStackTrace()) {
//						logger.error(" at "+ s);
//					}
				} catch (IOException e) {
					logger.error("IOException, "+ e);
//					for (StackTraceElement s: e.getStackTrace()) {
//						logger.error(" at "+ s);
//					}
				} catch (Exception e) {
					logger.error("Exception, "+ e);
//					for (StackTraceElement s: e.getStackTrace()) {
//						logger.error(" at "+ s);
//					}
				} finally {
					try { response1.close(); } catch (IOException e) {}
				}
	}

}
