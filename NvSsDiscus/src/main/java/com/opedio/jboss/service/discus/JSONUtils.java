package com.opedio.jboss.service.discus;

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


}
