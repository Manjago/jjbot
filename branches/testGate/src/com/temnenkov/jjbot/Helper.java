package com.temnenkov.jjbot;


public class Helper {
	
	public static String safeStr(String str) {
		return (str == null) ? "" : str;
	}
	
	public static boolean isEmpty(String str){
		return (str ==null) || (str.length() == 0);
	}
	
	public static String extractUser(String str){
	  if (isEmpty(str)) return "";
	  if (!str.contains("/")) return str;
	  int pos = str.indexOf("/");
	  return str.substring(0, pos);
	}

}
