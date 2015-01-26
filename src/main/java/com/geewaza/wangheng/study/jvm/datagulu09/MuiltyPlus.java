package com.geewaza.wangheng.study.jvm.datagulu09;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class MuiltyPlus {
	//N=3 30 300 1000
	static final int THREAD_COUNT = 1000;
	static final int M = 1000000;
	
	static Object lock = new Object();
	
	private static int count = 0;
	private static long start = 0L;
	
	static CountDownLatch begin = new CountDownLatch(THREAD_COUNT);
	static CyclicBarrier end = new CyclicBarrier(THREAD_COUNT, new Runnable() {
		
		@Override
		public void run() {
			System.out.println("耗时" + (System.currentTimeMillis()-start) + "ms, count = " + count);
		}
	});
	
	public static boolean increase() {
		count++;
		if (count >= M) {
			return false;
		}
		return true;
	}
	
	public static boolean increaseWithLock() {
		synchronized (lock) {
			count++;
		}
		if (count >= M) {
			return false;
		}
		return true;
	}
	
	
	static class PlusTask implements Runnable {

		@Override
		public void run() {
			try {
				begin.countDown();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			while (increase()) {
//			}
			while (increaseWithLock()) {
			}
			try {
				end.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		start = System.currentTimeMillis();
		for (int i = 0; i < THREAD_COUNT; i++) {
			new Thread(new PlusTask()).start();
		}
	}

}
