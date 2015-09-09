package com.example.util;

public class NetworkUtil {
	
	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >>> 8) & 0xFF) + "."
				+ ((i >>> 16) & 0xFF) + "." + (i >>>24 & 0xFF);
	}
	
}
