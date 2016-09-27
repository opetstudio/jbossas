package com.opedio.jboss.uxp.rest;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.opedio.jboss.lib.DataParameter;
import com.opedio.jboss.lib.JSONUtils;
import com.opedio.jboss.lib.MongodbUtil;
import com.opedio.jboss.lib.OpetUtil;
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
import java.util.Date;
import java.util.Map;

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


/**
 * Servlet implementation class RedeemManualServlet
 * developed by Nofrets Poai. 2015-10-23
 * API akan di hit dari uxp.
 * http://10.2.232.93:8080/SS/androidSyncNewerDiskusiURL?msisdn=85342805673&username=opet&password=opet&keyword=poin&channel=uxp
 */
//androidSyncNewerDiskusiURL
@WebServlet({ "/androidSyncNewerDiskusiURL", "/androidSyncNewerDiskusiURL/","/androidSyncNewerDiskusiURLServlet" })
public class androidSyncNewerDiskusiURLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static private Logger logger = Logger.getLogger(androidSyncNewerDiskusiURLServlet.class);
	/*static private String JMS_CONN_FACTORY = "java:/ConnectionFactory";
	static private String default_QUEUE = "queue/WFPoinSmsQueue";
	
	private QueueConnection queueConnection = null;
	private QueueSession queueSession       = null;
//	private MessageProducer sender = null;
	private QueueLogger producer_pool = null;*/
	
	private String _default_channel_name = "UXPREQ";
	
	private DataSource ds = null;
	
	private Map<String,PoinApiAccess> _access_list  = null;
	public static final String CHANNEL_LOOP_REDEEMCOUPON = "REFLEX-RM";
	
	 
	    private MongoDatabase mongoDB;
	    MongodbUtil mdbUtil;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public androidSyncNewerDiskusiURLServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
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
		
		param.set_trxid(getRequestParameter(request,"trxid"));
		param.set_keyword(getRequestParameter(request,"keyword"));
		param.set_callback(getRequestParameter(request,"CallbackUrl")); //CallbackUrl
		param.set_username(getRequestParameter(request,"username"));
		param.set_password(getRequestParameter(request,"password"));
		param.set_ip_addr(request.getRemoteAddr());
		param.set_ip_f5(param.get_ip_addr());
		
		long createdon = new Date().getTime();
//		var email = req.param('email');
		param.set_email(getRequestParameter(request,"email"));
		param.set_maximal_reload_newer(getRequestParameter(request,"maximal_reload_newer"));
		param.set_count_curr_rec(getRequestParameter(request,"count_curr_rec"));
		param.set_newer_modifiedon(getRequestParameter(request,"newer_modifiedon"));
		param.set_current_lesson_id(getRequestParameter(request,"current_lesson_id"));
		
		
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
			tag.put("email", param.get_email());
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
			JSONArray alldata = null;
			try {
				alldata = (JSONArray) new JSONParser().parse("[]");
			} catch (ParseException e1) {
				logger.error(""+ e1.getMessage());
//				for (StackTraceElement se: e1.getStackTrace()) {
//					logger.error(" at "+ se);
//				}
			}
			result.put("alldata", alldata);
			
			

//			else res.json({alldata:[]});
			
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
//					into_mongo(param);
					JSONArray ja = get_all_tb_diskusi(param,tag);
					if(ja.size() > 0){
						result = new JSONObject();
						result.put("alldata",ja);
						result.put("maximal_reload_newer",param.get_maximal_reload_newer());
						result.put("newer_modifiedon",param.get_newer_modifiedon());
	//					if(!e) res.json({maximal_reload_newer:maximal_reload_newer,newer_modifiedon:newer_modifiedon,alldata:o});
					}
					
				}
				else{
				}
				
				
				
				
				long end_time = System.currentTimeMillis();
				long dif_time = end_time - start_time;
				//end send to queue atau call procedure
				
//				tag.put("start_time", start_time);
//				tag.put("end_time", end_time);
//				tag.put("dif_time", dif_time);
//				tag.put("response", result);
//				logger.info(tag);
				
			} catch (Exception e) {
//				tag.put("response", result);				
//				logger.info(tag);
				logger.error("["+param.get_email()+"....] "+ e.getMessage());
				for (StackTraceElement se: e.getStackTrace()) {
					logger.error(param.get_email()+" at "+ se);
				}
			} finally {
				long end_time = System.currentTimeMillis();
				long dif_time = end_time - start_time;
				tag.put("dif", dif_time);
				logger.info(tag);
				out.println(result.toString());
				try {out.flush();out.close();} catch (Exception e2) {}
			}
			
			
	}
	
	public JSONArray get_all_tb_diskusi(DataParameter param,JSONObject tag){
		final JSONArray All_ja = new JSONArray();
		Document query = new Document();
		
//		DBCollection coll = mongoDB.getCollection("tb_diskusi");
		
		
		if(param.get_current_lesson_id() != null && "".equalsIgnoreCase(param.get_current_lesson_id()) && param.get_current_lesson_id().length() > 0){
//			query.lesson_id = current_lesson_id;
//			query.append("lesson_id",param.get_current_lesson_id());
			query.put("lesson_id", param.get_current_lesson_id());
			tag.put("lesson_id", param.get_current_lesson_id());
		}
		else{
			if(param.get_count_curr_rec() != null && param.get_maximal_reload_newer() != null && Integer.parseInt(param.get_count_curr_rec()) < Integer.parseInt(param.get_maximal_reload_newer())){
				// maximal_reload_newer = 400;
				// query = {};
			}
			else{
				// query = {modifiedon:{$gt: newer_modifiedon}};
				// query.modifiedon = {$gt: newer_modifiedon};
				
			}
			//TARU DISINI SUPAYA NDA TALALU BARAT BEBAN SERVER
//			query.modifiedon = {$gt: newer_modifiedon};
			tag.put("gt_modifiedon", param.get_newer_modifiedon());
			query.put("modifiedon", new Document("$gt",OpetUtil.parseLong(param.get_newer_modifiedon())));
		}
//		logger.info(param.get_email()+" androidSyncNewerDiskusiURL query = "+query.toString());
//		logger.info(param.get_email()+" androidSyncNewerDiskusiURL maximal_reload_newer = "+param.get_maximal_reload_newer());
		
		FindIterable<Document> iterable = mongoDB.getCollection("tb_diskusi").find(query);
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
				 
					 JSONObject jo = JSONUtils.jsonStringToJsonObject(document.toJson());
//					 logger.info("createdon: "+JSONUtils.jsonStringToJsonObject(""+jo.get("createdon")).get("$numberLong"));
//					 logger.info("status: "+jo.get("status"));
					 JSONObject doc = new JSONObject();
//					 Document doc = new Document();
					 	doc.put("_id", ""+JSONUtils.getIdStrFromMongoDbDoc(document));
					 	doc.put("lesson_id", ""+jo.get("lesson_id"));
		 				doc.put("lesson_id_inc", OpetUtil.parseInt(""+jo.get("lesson_id_inc")));
		 				doc.put("type", jo.get("type"));
		 				doc.put("status", OpetUtil.parseInt(""+jo.get("status")));
		 				doc.put("tanggal", jo.get("tanggal"));
		 				doc.put("senderEmail", jo.get("senderEmail"));
		 				doc.put("senderName", jo.get("senderName"));
		 				doc.put("MsgSenderMsisdn", jo.get("MsgSenderMsisdn"));
		 				doc.put("message", jo.get("message"));
		 				doc.put("createdon", OpetUtil.parseLong(""+JSONUtils.jsonStringToJsonObject(""+jo.get("createdon")).get("$numberLong")));
		 				doc.put("client", jo.get("client"));
		 				doc.put("createdonlocal", OpetUtil.parseLong(""+JSONUtils.jsonStringToJsonObject(""+jo.get("createdonlocal")).get("$numberLong")));
		 				doc.put("modifiedon", OpetUtil.parseLong(""+JSONUtils.jsonStringToJsonObject(""+jo.get("createdon")).get("$numberLong")));
		 				doc.put("time", OpetUtil.parseLong(""+JSONUtils.jsonStringToJsonObject(""+jo.get("createdon")).get("$numberLong")));
		 				doc.put("sqlite_id", OpetUtil.parseLong(""+JSONUtils.jsonStringToJsonObject(""+jo.get("sqlite_id")).get("$numberLong")));
					 
//					 All_ja.add(JSONUtils.jsonStringToJsonObject(doc.toJson()));
					 All_ja.add(doc);
				
//		        System.out.println(document.toJson());
//		        All_ja.add(document);
		       	}
		});
		 	return All_ja;
	}
	
	public String getRequestParameter(HttpServletRequest request, String parameter){
	    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	        if(entry.getKey().equalsIgnoreCase(parameter)){
	            return entry.getValue()[0];
	        }
	    }
	    return null;
	}
	
}
