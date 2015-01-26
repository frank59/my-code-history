package com.geewaza.wangheng.study.jvm.datagulu07;

import java.util.concurrent.TimeUnit;

public class DeadLockMain {
	
	private static Object A = new Object();
	private static Object B = new Object();
	private static Object C = new Object();
	private static Object D = new Object();
	
	public static void main(String[] args) {
		Thread threadA = new Thread(new ThreadTask(A, B), "threadA");
		Thread threadB = new Thread(new ThreadTask(B, C), "threadB");
		Thread threadC = new Thread(new ThreadTask(C, D), "threadC");
		Thread threadD = new Thread(new ThreadTask(D, A), "threadD");
		
		threadA.start();
		threadB.start();
		threadC.start();
		threadD.start();
	}
	
	static class ThreadTask implements Runnable {
		
		private Object lock1;
		private Object lock2;

		public ThreadTask(Object lock1, Object lock2) {
			this.lock1 = lock1;
			this.lock2 = lock2;
		}
		
		@Override
		public void run() {
			try {
				getLock(lock1, lock2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void getLock(Object lock1, Object lock2) throws InterruptedException {
			synchronized (lock1) {
				System.out.println(Thread.currentThread().getName() + "Get Lock lock1");
				TimeUnit.SECONDS.sleep(3);
				synchronized (lock2) {
					System.out.println(Thread.currentThread().getName() + "Get Lock lock2");
				}
			}
		}
		
	}

}
