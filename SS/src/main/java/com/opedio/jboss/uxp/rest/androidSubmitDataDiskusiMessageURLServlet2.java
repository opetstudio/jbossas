package com.opedio.jboss.uxp.rest;

import com.mongodb.client.MongoDatabase;
import com.opedio.jboss.lib.DataParameter;
import com.opedio.jboss.lib.JSONUtils;
import com.opedio.jboss.lib.MongodbUtil;
import com.opedio.jboss.lib.OpetDAO;
import com.solusi247.poin.data.PoinApiAccess;
import com.solusi247.poin.util.TselpoinIDGenerator;

import org.bson.Document;
import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

//import org.json.simple.JSONArray;
//import com.solusi247.poin.iprestrict.InjectApiIpRestrictSingleton;


/**
 * Servlet implementation class RedeemManualServlet
 * developed by Nofrets Poai. 2015-10-23
 * API akan di hit dari uxp.
 * http://10.2.232.93:8080/SS/androidSubmitDataDiskusiMessageURL?msisdn=85342805673&username=opet&password=opet&keyword=poin&channel=uxp
 */
//androidSubmitDataDiskusiMessageURL
@WebServlet({ "/androidSubmitDataDiskusiMessageURLServlet2", "/androidSubmitDataDiskusiMessageURLServlet2/","/androidSubmitDataDiskusiMessageURLServlet2" })
public class androidSubmitDataDiskusiMessageURLServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static private Logger logger = Logger.getLogger(androidSubmitDataDiskusiMessageURLServlet2.class);
	/*static private String JMS_CONN_FACTORY = "java:/ConnectionFactory";
	static private String default_QUEUE = "queue/WFPoinSmsQueue";
	
	private QueueConnection queueConnection = null;
	private QueueSession queueSession       = null;
//	private MessageProducer sender = null;
	private QueueLogger producer_pool = null;
	*/
	private String _default_channel_name = "UXPREQ";
	
	private DataSource ds = null;
	
	private OpetDAO opetDAO = null;
	private Map<String,PoinApiAccess> _access_list  = null;
	public static final String CHANNEL_LOOP_REDEEMCOUPON = "REFLEX-RM";
	    private MongoDatabase mongoDB;
	    MongodbUtil mdbUtil;
	

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public androidSubmitDataDiskusiMessageURLServlet2() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		InitialContext ictx = null;
		try {
			ictx = new InitialContext();
//			ds = (DataSource) ictx.lookup("java:/JNDITselpoinDS");
			
			
//			_access_list = InjectApiIpRestrictSingleton.get_access_list_ip();
//			openJmsConnection();
			mdbUtil = new MongodbUtil();
			mongoDB = MongodbUtil.getMongodb();
			
			opetDAO = new OpetDAO(mongoDB);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		logger.info("init ...");
	}
	
	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		logger.info("destroy  ...");
		
		try {
//			closeJmsConnection() ;
			mdbUtil.closeDB();
		} catch (Exception e) {
			logger.error("preDestroy, "+ e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}
		}
	}
	/*
	private void openJmsConnection() throws Exception {

		Context context = new InitialContext();


		QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(JMS_CONN_FACTORY);

		queueConnection = factory.createQueueConnection();

		//queueSession = queueConnection.createQueueSession(false, QueueSession.CLIENT_ACKNOWLEDGE);
		//queueSession = queueConnection.createQueueSession(false, QueueSession.DUPS_OK_ACKNOWLEDGE);
		queueSession = queueConnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);


		producer_pool = new QueueLogger();


		for (Map.Entry<String, String> entry :QueueLogger.get_keysMap().entrySet()) {
			Queue queue = (Queue) context.lookup(entry.getValue());

			MessageProducer sender = queueSession.createProducer(queue);

			sender.setDisableMessageID(true);
			sender.setDisableMessageTimestamp(true);

			producer_pool.add(entry.getKey(), sender);

		}

	}
	private void closeJmsConnection() throws Exception {
		//logger.debug("[stopApp] "+ JMS_QUEUE +" stopping");

		for (String catagory : producer_pool.keySet()) {
			MessageProducer sender = producer_pool.getMessageProducer(catagory);
			try {
				sender.close();
			} catch (Exception e) {
				logger.error("closeJmsConnection, "+ e);
			}
		}

		try { queueSession.close(); } catch (Exception e) {
			logger.error("closeJmsConnection, "+ e);
		}
		try { queueConnection.close(); } catch (Exception e) {
			logger.error("closeJmsConnection, "+ e);
		}
	}
*/

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
//		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
//		CallableStatement st = null;
//		java.sql.Connection conn = null;
		int status = 0;
		boolean is_async = true;
		
//		allData = JSON.parse(req.param('data'));
		
//		1. MSISDN
//		2. Channel
//		3. User
//		4.Password
//		5.TRXID
//		6. KEYWORD
		DataParameter param = new DataParameter();
		param.set_data_discus_json_str(getRequestParameter(request,"data"));
		param.set_msisdn(getRequestParameter(request,"msisdn"));
		param.set_trxid(getRequestParameter(request,"trxid"));
		param.set_keyword(getRequestParameter(request,"keyword"));
		param.set_callback(getRequestParameter(request,"CallbackUrl")); //CallbackUrl
		param.set_username(getRequestParameter(request,"username"));
		param.set_password(getRequestParameter(request,"password"));
		param.set_ip_addr(request.getRemoteAddr());
		param.set_ip_f5(param.get_ip_addr());
		if(!"".equals(request.getHeader("X-Forwarded-For")) && request.getHeader("X-Forwarded-For") != null){
			param.set_ip_addr(request.getHeader("X-Forwarded-For"));
		}
		
			String _p_msisdn = param.get_msisdn();
			String _p_channel = getRequestParameter(request,"channel");
			if (_p_channel==null || "".equalsIgnoreCase(_p_channel)) 
				_p_channel = this._default_channel_name;
			param.set_channel(_p_channel);
			//normalisasi msisdn
			if(_p_msisdn != null){
				if (_p_msisdn.startsWith("62")) {
					_p_msisdn = _p_msisdn.substring(2);
				} else if (_p_msisdn.startsWith("08")) {
					_p_msisdn = _p_msisdn.substring(1);
				} else if (_p_msisdn.startsWith("+62")) {
					_p_msisdn = _p_msisdn.substring(3);
				}
				_p_msisdn = "62"+_p_msisdn;
			}
			param.set_msisdn(_p_msisdn);
			long _msg_id = TselpoinIDGenerator.getNextTselpoinID();
			param.set_msg_id(_msg_id);
			long start_time = System.currentTimeMillis();
			JSONObject tag = new JSONObject();
//			tag.put("ip_addr", param.get_ip_addr());
//			tag.put("ip_f5", param.get_ip_f5());
//			tag.put("msg_id", param.get_msg_id());
//			tag.put("msisdn", _p_msisdn);
//			tag.put("trxid", _p_trxid);
//			tag.put("keyword", param.get_keyword());
//			tag.put("request", request.getQueryString());
//			tag.put("user", param.get_username());
//			tag.put("pass", param.get_password());
			
			JSONObject result = new JSONObject();
//			result.put("msisdn", _p_msisdn);
//			result.put("notification", "Selamat Anda Telah menukarkan poin dengan voucher : XXX8899. Silahkan SMS ini deiperlihatkan");
//			result.put("verification_flag", "Y");
//			result.put("trx_id", param.get_trxid());
//			result.put("err_code", "000");
//			result.put("err_msg", "Success");
			result.put("status", "OK");
			
			
			//validasi user/password
			if (!param.isAuth()){
//				result.put("err_code", "001");
//				result.put("err_msg", "Authentication error");
//				tag.put("response", result);
//				logger.info(tag);
//				out.println(result.toString());
//				return;
			}
			//validasi parameter mandatori
			if(!param.isParamComplete()){
//				result.put("err_code", "004");
//				result.put("err_msg", "Parameter incomplete");
//				tag.put("response", result);
//				logger.info(tag);
//				out.println(result.toString());
//				return;
			}
			
			try {
				//send to queue atau call procedure
				if(is_async){
					//async method
					
					String m = "1|"+ _p_msisdn +"|"+param.get_keyword()+"|"+param.get_callback();
//					sms_queue(m, _p_msisdn, _p_channel, _msg_id, param.get_trxid());
					//insert into mongodb
//					into_mongo(param,jboss_createdon);
//					plsqlproc.into_tb_diskusi(param,jboss_createdon);
					TimeUnit.SECONDS.sleep(1);
					insert_tb_diskusi(param);
					
					
				}
				
				long end_time = System.currentTimeMillis();
				long dif_time = end_time - start_time;
				
			} catch (Exception e) {
//				result.put("err_code", "003");
//				result.put("err_msg", "internal error");
				result.put("status", "NOK");
//				tag.put("response", result);
//				out.println(result.toString());
				logger.info(tag);
				logger.error("["+_p_msisdn+"] "+ e.getMessage());
				for (StackTraceElement se: e.getStackTrace()) {
					logger.error(_p_msisdn+" at "+ se);
				}
			} finally {
	//			if (st != null){try {st.close();}catch(SQLException e){}}
	////			if (ps != null){ try {ps.close();}	catch(SQLException e){}	}
	//			if (conn!=null){ try{conn.close();} catch (SQLException e) {}}
				out.println(result.toString());
				try {out.flush();out.close();} catch (Exception e2) {}
			}
	}
	void sms_queue(String m, String _p_msisdn, String _p_channel, long _msg_id, String trx_id) throws Exception {
		logger.info(_p_msisdn+" prepare sender");
		/*MessageProducer sender = producer_pool.getMessageProducer("DISCUSMSG");
			TextMessage msg = queueSession.createTextMessage(m);
			msg.setLongProperty(PoinConstants.jms_TSELPOIN_ID, _msg_id);
			msg.setStringProperty(PoinConstants.jms_REQ_CHANEL, _p_channel);
			msg.setStringProperty("UXP_TRXID", trx_id);
			msg.setStringProperty("CALLBACK_METHOD", "post");
			logger.info(_p_msisdn+" ready to send message");
			sender.send(msg);
			logger.info(_p_msisdn+" send message to queue/WFSSDiscusMsgQueue");*/
//			PoinSms._krome_queue_in_counting(); //
	}
	public void into_mongo(DataParameter param, long jboss_createdon){
		
		if(!JSONUtils.isValid(param.get_data_discus_json_str())){
			logger.error("data is not json string");
		}
		else{
			JSONParser parser = new JSONParser();
			Object obj;
			JSONArray ja = null;
			try {
				obj = parser.parse(param.get_data_discus_json_str());
				ja = (JSONArray) obj;
//				result.put("poin", ja);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
				logger.error(e1);
			}
			try {
				
//				mongoDB.requestStart();
//				DBCollection coll = mongoDB.getCollection("WFSSDiscusMsgCollection");
				Document doc = new Document();
				doc.put("data", ja);
	            doc.put("createdon", jboss_createdon);
	           /* BasicDBObject info = new BasicDBObject();
	            info.put("x", 203);
	            info.put("y", 102);
	            doc.put("info", info);*/
	            mongoDB.getCollection("WFSSDiscusMsgCollection").insertOne(doc);
			} catch (Exception e) {
				logger.error(""+ e.getMessage());
				for (StackTraceElement se: e.getStackTrace()) {
					logger.error(" at "+ se);
				}
			} finally{
				 try {
//					 mongoDB.requestDone();
				} catch (Exception e2) {
				}
			}
			
		}
		//
		
	}
	
	public void insert_tb_diskusi(DataParameter param){
		logger.info("ceeeeeeeeek:"+param.get_data_discus_json_str());
		if(!JSONUtils.isValid(param.get_data_discus_json_str())){
			logger.error("data is not json string");
		}
		else{
			JSONArray ja = null;
			try {
				ja = (JSONArray) new JSONParser().parse(param.get_data_discus_json_str());
				if(ja == null) return;
				
				logger.info(ja.toString());
			} catch (ParseException e1) {
//				e1.printStackTrace();
				logger.error(e1.getCause());
			}
//			opetDAO.batch_insert_tb_diskusi(ja,true);
		}
	}
	
	public String getRequestParameter(HttpServletRequest request, String parameter){
		
		
	    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	    	logger.info("entry.getKey():"+entry.getKey());
	        if(entry.getKey().equalsIgnoreCase(parameter)){
	            return entry.getValue()[0];
	        }
	    }
	    return null;
	}
	
}
