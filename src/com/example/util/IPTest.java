package com.example.util;

import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;

public class IPTest {
	public static final int PORT = 445;//smb·þÎñ¶Ë¿ÚºÅ
	public static final int TIMEOUT = 500;

	private IPTest() {
	}
	
	public static String test(String ip) {
		try {
			testwithThrows(ip);
		} catch (Exception e) {
//			Log.i(MyTag.myTest, e.toString());
			ip="";
		}
		return ip;
	}
	
	public static String testwithThrows(String ip) throws Exception {
		InetSocketAddress remoteAddr = new InetSocketAddress(ip, PORT);
		Socket s = new Socket();
		try {
			s.connect(remoteAddr, TIMEOUT);
			return ip;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			s.close();
		}
		
	}

}
