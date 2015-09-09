package com.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 查找可用的IP地址
 * @author psd
 *
 */
public class FindUsefulIP {

	public String ip;
	// public String netmask;
	static int threadCount = 0;
	private ArrayList<String> possibleIPList = new ArrayList<String>();
	private ArrayList<String> IpList = new ArrayList<String>();
	List<Future<String>> list = new ArrayList<Future<String>>();

	public FindUsefulIP(String ip) {
		this.ip = ip;
		// this.netmask = NetMask;
	}

	public void seek() throws InterruptedException, ExecutionException {
		getPossibleIpList();
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < possibleIPList.size(); i++) {
			final String ip = possibleIPList.get(i);
			Future<String> future = pool.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return IPTest.test(ip);
				}
			});
			
			list.add(future);
		}

		pool.shutdown();

		for (Future<String> f : list) {
			if (!f.get().toString().equals("")) {
				IpList.add(f.get().toString());
			}
		}
	}

	private void getPossibleIpList() {
		String temp[] = ip.split("\\.");
		String possibleIP = temp[0] + "." + temp[1] + "." + temp[2] + ".";
		int i = Integer.parseInt(temp[3]); // IP地址后三位
		for (int j = 1; j < 255; j++) {
			if (j != i)
				possibleIPList.add(possibleIP + String.valueOf(j));
		}
	}
	
	public ArrayList<String> getIpList() {
		return IpList;
	}

}
