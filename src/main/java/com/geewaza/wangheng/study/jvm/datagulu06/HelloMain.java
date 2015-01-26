package com.geewaza.wangheng.study.jvm.datagulu06;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class HelloMain {

	public static void main(String[] args) throws Exception {
		while (true) {
			
			MyClassLoader myClassLoader = new MyClassLoader("D:\\workspace\\study-project\\target\\classes\\", new String[]{"com.geewaza.wangheng.study.jvm.datagulu06.Work"});
			
			Class<?> cls = myClassLoader.loadClass("com.geewaza.wangheng.study.jvm.datagulu06.Work");
			Object obj = cls.newInstance();
			Method m = cls.getMethod("doit", new Class[]{});
			m.invoke(obj, new Object[]{});
			TimeUnit.SECONDS.sleep(1);
		}
	}

}
