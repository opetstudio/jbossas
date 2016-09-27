package com.opedio.jboss.lib;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;

public class MongodbUtil {

	 private MongoClient mongo;
//	 private DB mongoDB;
	 private MongoDatabase mongoDB;
	 String host;
	 String sport;
	 int port;
	 String db;
	 String user;
	 String password;
	 private boolean isdev = OpetUtil.isdev;
	 

	public MongodbUtil() {
		// TODO Auto-generated constructor stub
	}
	public static MongoDatabase getMongodb(){
		MongodbUtil me1 = new MongodbUtil();
		return me1.openDB();
	}
	public MongoDatabase openDB(){
	        this.setAccount();
	        
	        if(isdev) mongo = new MongoClient(new ServerAddress(host, port));
			else{
				MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
				mongo = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
			}
			mongoDB = mongo.getDatabase(db);
			return mongoDB;
	  }
	private void setAccount(){
		if(!isdev){
			host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");// //OPENSHIFT_MONGODB_DB_HOST //127.12.215.130
			if(host==null) host = System.getenv("OPENSHIFT_JBOSSAS_IP");
	        sport = System.getenv("OPENSHIFT_MONGODB_DB_PORT"); //OPENSHIFT_MONGODB_DB_PORT //27017
	        if(sport==null) sport= "45896";
	        port = Integer.decode(sport);
	        db = System.getenv("OPENSHIFT_APP_NAME");
	        if(db == null) db = "jbossas";
	        user = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
	        if(user == null) user = "admin";
	        password = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
	        if(password == null) password = "NkfU6wGaDeG-";
		}
		else{
			host = "localhost";
	        sport = "27017";
	        port = Integer.decode(sport);
	        db = "jboss_mmm";
	        user = "";
	        password = "";
		}
	  
	}
	public void closeDB(){
		  try {
				if(mongo != null)
					mongo.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
	public MongoClient getMongo() {
		return mongo;
	}
	public void setMongo(MongoClient mongo) {
		this.mongo = mongo;
	}
	public MongoDatabase getMongoDB() {
		return mongoDB;
	}
	public void setMongoDB(MongoDatabase mongoDB) {
		this.mongoDB = mongoDB;
	}
	  

}
