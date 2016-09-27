package com.opedio.jboss.lib;

public class OpetUtil {
	
//	public static final String TOPDOMAIN = "http://10.0.2.2:8000";
//  public static final String TOPDOMAIN = "http://www.sekolahsabat.com";
  public static final String TOPDOMAIN = "http://homepage-opetstudio.rhcloud.com";
//  public static final String TOPDOMAIN = "http://ssdev-gmahkssdev.rhcloud.com";
  //
//  public static final String TOPDOMAIN2 = "http://10.0.2.2:8085";
  public static final String TOPDOMAIN2 = "http://jbossas-eastvoice.rhcloud.com";
  
//URL of the PHP API
  private static String loginURL = TOPDOMAIN+"/androidloginapi/";
  private static String registerURL = TOPDOMAIN+"/androidloginapi/";
  private static String forpassURL = TOPDOMAIN+"/androidloginapi/";
  private static String chgpassURL = TOPDOMAIN+"/androidloginapi/";
////  
  private static String fetchNewMessageURL = TOPDOMAIN+"/androidsyncmessage/";
  private static String androidsyncdataURL = TOPDOMAIN+"/androidsyncdata/";
  private static String androidSyncNewerChannelURL = TOPDOMAIN+"/androidSyncNewerChannelURL/";
  private static String androidSyncOlderChannelURL = TOPDOMAIN+"/androidSyncOlderChannelURL/";
  private static String androidSyncNewerGwURL = TOPDOMAIN+"/androidSyncNewerGwURL/";
  
  private static String androidSyncNewerSSLessonsURL = TOPDOMAIN+"/androidSyncNewerSSLessonsURL/";
  private static String androidSyncNewerSSLessonsContentURL = TOPDOMAIN+"/androidSyncNewerSSLessonsContentURL/";
  private static String androidSyncNewerRenunganpagiURL = TOPDOMAIN+"/androidSyncNewerRenunganpagiURL/";
  private static String androidSyncNewerAlkitabURL = TOPDOMAIN+"/androidSyncNewerAlkitabURL/";
//  private static String androidSyncNewerDiskusiURL = TOPDOMAIN+"/androidSyncNewerDiskusiURL/";
  private static String androidSyncNewerDiskusiURL = TOPDOMAIN2+"/SS/androidSyncNewerDiskusiURL/";


  private static String androidSyncNewerAkunkuURL = TOPDOMAIN+"/androidSyncNewerAkunkuURL/";
  private static String androidSubmitDataProfileURL = TOPDOMAIN+"/androidSubmitDataProfileURL/";
  private static String registerbyemail = TOPDOMAIN+"/gcmserver/registerbyemail/";
  private static String androidSubmitDataDiskusiMessageURL = TOPDOMAIN+"/androidSubmitDataDiskusiMessageURL/";
//  private static String androidSubmitDataDiskusiMessageURL = TOPDOMAIN2+"/SS/androidSubmitDataDiskusiMessageURL";



  public static String androidSubmitDataDiskusiAndGetNewMessageURL = TOPDOMAIN+"/androidSubmitDataDiskusiAndGetNewMessageURL/";




  private static String androidSyncNewerDaftarisiURL = TOPDOMAIN+"/androidSyncNewerDaftarisiURL/";
  private static String androidSyncNewerNotifyURL = TOPDOMAIN+"/androidSyncNewerNotifyURL/";
  
  public static boolean isdev = false; //kalo mau develope....ganti ke true

	public OpetUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static int parseInt(String str){
		try {
			return Integer.parseInt(str != null && !"".equalsIgnoreCase(str) ? str:"0");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return 0;
	}
	public static long parseLong(String str){
		try {
			return Long.parseLong(str != null && !"".equalsIgnoreCase(str) ? str:"0");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return 0;
	}

}
