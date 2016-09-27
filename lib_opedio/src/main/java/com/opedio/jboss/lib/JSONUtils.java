package com.opedio.jboss.lib;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtils {

	  private JSONUtils(){}

	  public static boolean isValid(String json) {
		  json = json.trim();
		  boolean result = false;
		  if(json==null){
			  result = false;
		  }
		  else if (json.startsWith("{") && json.endsWith("}")) {
			  result = true;
		  }
		  else if(json.startsWith("[") && json.endsWith("]")){
			  result = true;
		  }
		  else{
			  result = false;
		  }
		 return result;
	}
	public static JSONObject jsonStringToJsonObject(String jsonString){
		JSONParser parser = new JSONParser();
		try {
			JSONObject jo = (JSONObject) parser.parse(""+jsonString);
			return jo;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ObjectId getDocIdFromJsonArr(JSONArray All_ja,int index){
		JSONObject jo = new JSONObject();
		JSONObject oid = new JSONObject();
		
		jo = (JSONObject) All_ja.get(index);
		oid = (JSONObject) jo.get("_id");
		
		ObjectId _id = new ObjectId(""+oid.get("$oid"));
		
		return _id;
	}
	public static String getIdStrFromMongoDbDoc(Document doc){
		JSONParser parser = new JSONParser();
		JSONObject jo,oid;
		try {
			jo = (JSONObject) parser.parse(""+doc.toJson());
			oid = (JSONObject) jo.get("_id");
			return ""+oid.get("$oid");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}


}
