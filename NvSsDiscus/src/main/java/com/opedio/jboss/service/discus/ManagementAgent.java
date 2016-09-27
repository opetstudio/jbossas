package com.opedio.jboss.service.discus;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import org.jboss.logging.Logger;

import java.util.Arrays;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ManagementAgent implements ManagementAgentMBean {
	
	private Logger logger = Logger.getLogger(ManagementAgent.class);
	
	private String serviceName = "SsDiscus";

    private String lifecycleString = "NULL";

    private int thread_number = Runtime.getRuntime().availableProcessors();
    private int queue_size = 3 *thread_number;
    private int max_row_num = 100;
    private int timer = 6000; //6 seconds
    private DataSource ds = null;
    private MongoClient mongo = null;
    private DB mongoDB;
    private DataDiscusSender client;
     
    public  String getServiceName() {
    	logger.info("getServiceName ..... " + this.serviceName);
    	return this.serviceName;
    }
    
    public String getStateString() {
    	return this.lifecycleString;
    }
    
    
    public void setThreadNumber(int thread_number) {
    	if (thread_number>0) 
    		this.thread_number = thread_number;
		logger.info("setThreadNumber ..... " +  this.thread_number);

    }
    
    public int getThreadNumber() {
    	return this.thread_number;
    }
    
	private Runnable _lazy_start = new  Runnable() {

		public void run() {
			logger.info("[_lazy_start]  ... starting");

			int i = 0;
			boolean got_datasource = false;
			while (!got_datasource) {
				try {
					logger.warn("_lazy_start looking up " + "java:/JNDITselpoinDS" +" try="+ i++);

					InitialContext ictx = new InitialContext();
//					ds = (DataSource) ictx.lookup("java:/JNDITselpoinDS");
					openMongoDbClien();
					got_datasource = true;

				} catch (NamingException e) {
					logger.error("_lazy_start NamingException: "+ e);
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e2) {
						logger.warn("_lazy_start, "+e2);
					}

				} catch (Exception e) {
					logger.error("_lazy_start Creating Exception: "+ e);
					for (StackTraceElement s: e.getStackTrace()) {
						logger.error(" at "+ s);
					}
				}
			}
			
			try {
				//	public SmsSender(int thread_number, int queue_size, int row_num, int timer, DataSource datasource) {
					
					client = new DataDiscusSender(thread_number,queue_size,max_row_num,timer,ds, mongoDB);
					client.start();

				} catch (Exception e) {
					logger.error("[Exception on starting TelnetClient] "+  e);
					for (StackTraceElement s: e.getStackTrace()) {
						logger.error(" at "+ s);
					}
				}

			
			

			logger.info("_lazy_start DONE");
		}
	};




    /**
    * The lifecycle operations
    * 
    */
    
    public void create() throws Exception {
		logger.info("Creating ..... " + serviceName);
		if ("created".equals(lifecycleString)) {
			logger.info(serviceName + "... already created");
			return;
		}
		
		
		lifecycleString = "created";
		logger.info("Created .... " + serviceName);
    }
    
    public void start() throws Exception {
    	
    	
		// TODO Auto-generated method stub

		logger.info("Starting ..... " + serviceName);

		if (!"created".equals(lifecycleString)) {
			create();
		}
		
		if ("started".equals(lifecycleString)) {
			logger.info(serviceName + "... already started");
			return;
		} 

		
		new Thread(_lazy_start).start();

		//	public SmsSender(int thread_number, int queue_size, int row_num, int timer, DataSource datasource) {
 
		lifecycleString = "started";
		logger.info("Started .... " + serviceName);

	}
    
    
    
    
    public void stop() {
		logger.info("Legacy Stopping .... " + serviceName);
		
		if ("stopped".equalsIgnoreCase(lifecycleString)) {
			logger.info(serviceName + "... already stopped");
			return;
		}

		
		try {

			client.ophouden();
			client.interrupt();

		} catch (Exception e) {
			logger.error("[stoping] "+  e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}
		}

    	lifecycleString = "stopped";
		logger.info("Stopped .... " + serviceName);
    }
    
    
    public void destroy() throws Exception {
    	
	logger.info("Destroying .... " + serviceName);
		
		if (!"stopped".equalsIgnoreCase(lifecycleString)) 
			stop();
		
		if ("destroyed".equalsIgnoreCase(lifecycleString)) {
			logger.info(serviceName + "... already destroyed");
			return;
		}
		
		// destroying
		
		try {

			if (client!=null && client.isAlive()) {
				client.join();
			}

		} catch (Exception e) {
			logger.error("[destroying] "+  e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}
		}

		
		closeMongoDbClient();
		
		lifecycleString = "destroyed";
		logger.info("Destroyed .... " + serviceName);
    }
    
    public void openMongoDbClien(){
		logger.info("connectMongoDb ... ");
		String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");// //OPENSHIFT_MONGODB_DB_HOST //127.12.215.130
		if(host==null) host = System.getenv("OPENSHIFT_JBOSSAS_IP");
		
        String sport = System.getenv("OPENSHIFT_MONGODB_DB_PORT"); //OPENSHIFT_MONGODB_DB_PORT //27017
        if(sport==null) sport= "27017";
        int port = Integer.decode(sport);
		/*MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(host , port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		
		
        
        String db = System.getenv("OPENSHIFT_APP_NAME");
        if(db == null)
            db = "mydb";
        String user = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
        if(user == null) user = "admin";
        String password = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
        if(password == null) password = "UGC-a91KDtSE";
        
        logger.info("host:"+host+" sport:"+sport+" db:"+db+" user="+user+" pass="+password);
        
        

       /* try {
//            mongo = new MongoClient(host , port);
            mongo = new MongoClient(Arrays.asList(new ServerAddress(host, port)));
        } catch (UnknownHostException e) {
        	logger.error("UnknownHostException, "+ e);
			for (StackTraceElement s: e.getStackTrace()) {
				logger.error(" at "+ s);
			}
            try {
//				throw new ServletException("Failed to access Mongo server", e);
			} catch (ServletException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }*/
        
        
        MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());
        try {
			mongo = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mongoDB = mongo.getDB(db);
        
        /*if(mongoDB.authenticate(user, password.toCharArray()) == false) {
        	logger.error("mongoDB authentication = false");
           
        }*/
	}
	public void closeMongoDbClient(){
		try {
			if(mongo != null)
				mongo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
