package com.opedio.jboss.uxp.rest;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;


import javax.jms.MessageProducer;

public class QueueLogger {

	//private ConcurrentMap<String,MessageProducer> _senders = null;
	private Map<String,MessageProducer> _senders =  new LinkedHashMap<String,MessageProducer>();
	private List<String> keys = new LinkedList<String>();

	private static final  Map<String,String> _keys = new HashMap<String,String>();

	public QueueLogger() {
//		_keys.put("LOGGER", "queue/WFSmsLoggerQueue");
//		_keys.put("KROME", "queue/WFSmsUnifierQueue");
//		_keys.put("SMSQ", "queue/WFPoinSmsQueue");
		_keys.put("DISCUSMSG", "queue/WFSSDiscusMsgQueue");
//		_keys.put("CALLBACK", "queue/WFCallbackNewQueue");
	}

	public static Map<String,String> get_keysMap() {
		return _keys;
	}
	 
	 
	public void add(String key,MessageProducer message_producer) {
		_senders.put(key, message_producer);
		keys.add(key);
	}
	

	public MessageProducer getMessageProducer(String key) {
		return _senders.get(key);
	}
	
	
	
	public Set<String> keySet() {
		
		return _senders.keySet();
		
	}
	
	
	

	
}
