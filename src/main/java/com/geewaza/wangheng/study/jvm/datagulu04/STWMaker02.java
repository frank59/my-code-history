package com.geewaza.wangheng.study.jvm.datagulu04;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class STWMaker02 {
	
	private int size = 512;
	private int maxMemorry = 450; //M
			
	private Set<byte[]> set = new HashSet<byte[]>();
	
	public void makeSTW() {
		
		try {
			while (true) {
				if (set.size() * size / 1024 / 1024 >= maxMemorry) {
					System.out.println("开始清理内存：");
					set.clear();
				}
				
				for (int i = 0; i < 1024; i++) {
					set.add(new byte[size]);
				}
				TimeUnit.MILLISECONDS.sleep(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		STWMaker02 maker02 = new STWMaker02();
		maker02.makeSTW();
	}

}
