package com.geewaza.wangheng.study.jvm.datagulu04;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class STWMaker {
	
	
	private static int _1MB = 1024 * 1024;
	
	private static Set<byte[]> set = new HashSet<byte[]>();
	
	public static void main(String[] args) throws InterruptedException {
		while (true) {
			if (set.size() > 90) {
				set.clear();
			} else {
				set.add(new byte[_1MB]);
			}
			System.out.println("set.size = " + set.size());
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

}
