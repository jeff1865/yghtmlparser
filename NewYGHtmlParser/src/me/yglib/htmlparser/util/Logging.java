package me.yglib.htmlparser.util;

// need to add logger observer
public class Logging {
	
	public static int DEBUG = 0x01;
	public static int INFO = 0x10;
	public static int ERROR = 0xF0;
	
	private static long latestTime = System.currentTimeMillis();;
	
	public static void print(int level, String msg)
	{
		if(level < 0x10) return;
		long ct = System.currentTimeMillis();
		System.out.println("[LOG:" + (ct - latestTime) + "] " + msg);
		latestTime = ct;
	}
	
	public static void debug(String msg){
		print(Logging.DEBUG, msg);
	}
}
