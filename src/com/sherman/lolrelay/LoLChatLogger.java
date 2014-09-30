package com.sherman.lolrelay;

import java.util.Calendar;

public class LoLChatLogger {
	
	public static boolean[] flags = {true, true, true, true};
	public static final int ERROR_FLAG = 0;
	public static final int DEBUG_FLAG = 1;
	public static final int WARNING_FLAG = 2;
	public static final int NOTICE_FLAG = 3;
	
	public static void logError(String error){
		if(flags[ERROR_FLAG])
			System.err.println("ERROR (" + Calendar.getInstance().getTime().toString() +"):\t " + error);
	}
	public static void logDebug(String debug){
		if(flags[DEBUG_FLAG])
			System.out.println("DEBUG (" + Calendar.getInstance().getTime().toString() +"):\t " + debug);
	}
	public static void logWarning(String warning){
		if(flags[WARNING_FLAG])
			System.out.println("WARNING (" + Calendar.getInstance().getTime().toString() +"):\t " + warning);
	}
	public static void logNotice(String notice){
		if(flags[NOTICE_FLAG])
			System.out.println("NOTICE (" + Calendar.getInstance().getTime().toString() +"):\t " + notice);
	}
}
