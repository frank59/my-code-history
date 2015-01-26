package com.geewaza.wangheng.study.jvm.datagulu10;

public class HelloWorld implements Runnable{

	private String hello;
	private String world;
	
	public String getHello() {
		return hello;
	}
	
	public void setHello(String hello) {
		this.hello = hello;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	@Override
	public void run() {
		System.out.println("Hello World！");
	}

	public HelloWorld(String hello, String world) {
		this.hello = hello;
		this.world = world;
	}
}
