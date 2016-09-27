package com.opedio.jboss.lib;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Date;



public class OpetDAO {
	
	private MongoDatabase mongoDB = null;
	
	private static Logger logger = Logger.getLogger(OpetDAO.class);
	private String _client = "jboss";

	public OpetDAO() {
	}
	public OpetDAO(MongoDatabase mongoDB) {
		this.mongoDB = mongoDB;
	}
	
	public Document get_tb_diskusi_doc(JSONObject jo){
		Document doc = new Document();
		String client = ""+jo.get("client");
		long sqlite_id = 0;
		long createdon = new Date().getTime();
		long modifiedon = new Date().getTime();
		ObjectId _id = new ObjectId();
		
		if(client != null && "nodejs".equalsIgnoreCase(client)){
			sqlite_id = Long.parseLong(""+jo.get("sqlite_id"));
			createdon = OpetUtil.parseLong(""+jo.get("createdon"));
			_id = new ObjectId(""+jo.get("_id"));
		}
		else{
			//dari android
			sqlite_id = Long.parseLong(""+jo.get("_id"));
		}
		
		doc.put("lesson_id", ""+jo.get("lesson_id"));
		doc.put("lesson_id_inc", OpetUtil.parseInt(""+jo.get("lesson_id_inc")));
		doc.put("type",  OpetUtil.parseInt(""+jo.get("type")));
		doc.put("status", 2);
		doc.put("tanggal", jo.get("tanggal"));
		doc.put("senderEmail", jo.get("senderEmail"));
		doc.put("senderName", jo.get("senderName"));
		doc.put("MsgSenderMsisdn", jo.get("MsgSenderMsisdn"));
		doc.put("message", jo.get("message"));
		doc.put("createdon", createdon);
		doc.put("client", this._client);
		doc.put("client_from", client);
		doc.put("createdonlocal", OpetUtil.parseLong(""+jo.get("createdonlocal")));
		doc.put("modifiedon", modifiedon);
		doc.put("time", OpetUtil.parseLong(""+jo.get("createdon")));
		doc.put("sqlite_id", sqlite_id);
		doc.put("_id", _id);
		
		return doc;
	}
	public void into_tb_diskusi(DataParameter param,long jboss_createdon){
		if(!JSONUtils.isValid(param.get_data_discus_json_str())){
			logger.error("data is not json string");
		}
		else{
			JSONArray ja = null;
			try {
				ja = (JSONArray) new JSONParser().parse(param.get_data_discus_json_str());
				if(ja == null) return;
			} catch (ParseException e1) {
//				e1.printStackTrace();
				logger.error(e1.getCause());
			}
			try {
				
				Document doc = new Document();
				for (int i = 0; i < ja.size(); i++) {
					try {
						JSONObject jo = (JSONObject) new JSONParser().parse(""+ja.get(i));
						doc = this.get_tb_diskusi_doc(jo);
//						 insert_tb_diskusi(doc);
						 insert_tb_diskusi_copy(doc);
					} catch (Exception e) {
						logger.error(""+ e.getMessage());
						for (StackTraceElement se: e.getStackTrace()) {
							logger.error(" at "+ se);
						}
					}
					
				}
			    
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
		
	}

	public void batch_insert_tb_diskusi(JSONArray ja, boolean isCopy){
		for (int i = 0; i < ja.size(); i++) {
			Document doc = new Document();
			try {
				JSONObject jo = (JSONObject) new JSONParser().parse(""+ja.get(i));
				doc = get_tb_diskusi_doc(jo);
				this.insert_tb_diskusi(doc);
				if(isCopy) this.insert_tb_diskusi_copy(doc);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void insert_tb_diskusi(Document doc){
//		DBCollection coll = mongoDB.getCollection("tb_diskusi");
		
		 Document query = new Document("senderName", doc.get("senderName"))
			.append("createdonlocal",OpetUtil.parseLong(""+doc.get("createdonlocal")))
			.append("message",doc.get("message"));
		 
		 FindIterable<Document> iterable = mongoDB.getCollection("tb_diskusi").find(query);
		 final JSONArray All_ja = new JSONArray();
		 iterable.forEach(new Block<Document>() {
			    @Override
			    public void apply(final Document document) {
//			        System.out.println(document);
			        All_ja.add(JSONUtils.jsonStringToJsonObject(document.toJson()));
			    }
			});
		 if(All_ja.size() > 0){
			 //update
			 logger.info("do update tb_diskusi exist|"+doc.get("client_from")+"|"+doc.get("senderName")+"|"+doc.get("createdonlocal")+"|"+doc.get("message"));
         	//lakukan update
         	ObjectId _id = new ObjectId(""+doc.get("_id"));
         	doc.remove("_id");
         	mongoDB.getCollection("tb_diskusi").updateOne(new Document("_id", _id), new Document("$set", doc));
         	
		 }
		 else{
			 //insert
			 mongoDB.getCollection("tb_diskusi").insertOne(doc);
         	logger.info("tb_diskusi notexist|"+doc.get("client_from")+"|"+doc.get("senderName")+"|"+doc.get("createdonlocal")+"|"+doc.get("message"));
		 }
		 
		/* DBCursor cursor = coll.find(query);
		 try {
	            if (!cursor.hasNext()) {
	            	mongoDB.getCollection("tb_diskusi").insertOne(doc);
	            	logger.info("tb_diskusi notexist|"+doc.get("client_from")+"|"+doc.get("senderName")+"|"+doc.get("createdonlocal")+"|"+doc.get("message"));
	            }else{
	            	logger.info("do update tb_diskusi exist|"+doc.get("client_from")+"|"+doc.get("senderName")+"|"+doc.get("createdonlocal")+"|"+doc.get("message"));
	            	//lakukan update
	            	ObjectId _id = new ObjectId(""+doc.get("_id"));
	            	doc.remove("_id");
	            	mongoDB.getCollection("tb_diskusi").updateMany(new Document("_id", _id), new Document("$set", doc));
	            }
	     	}catch (Exception e) {
				logger.error(doc.get("senderEmail")+" "+e.getMessage());
	        } finally {
	        	cursor.close();
	        }*/
		
	}
	public void insert_tb_diskusi_copy(Document doc){
		mongoDB.getCollection("tb_diskusi_copy").insertOne(doc);
	}
	
	public static String get_local_client(){
		OpetDAO me1 = new OpetDAO();
		return me1._client;
	}


}
