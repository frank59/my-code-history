package com.geewaza.wangheng.study.jvm.datagulu06;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MyClassLoader extends ClassLoader{
	
	private String dir;
	private Set<String> clazzes;

	public MyClassLoader(String dir, String[] clazzes) throws IOException {
		super(null);
		this.dir = dir;
		this.clazzes = new HashSet<String>();
		loadClassByName(clazzes);
	}
	
	private void loadClassByName(String[] classNames) throws IOException {
		for (int i = 0; i < classNames.length; i++) {
			loadClazz(classNames[i]);
			clazzes.add(classNames[i]);
		}
	}
	
	private Class<?> loadClazz(String name) throws IOException {
		Class<?> cls = null;
		StringBuilder sb = new StringBuilder(dir);
		String className = name.replace(".", File.separatorChar + "") + ".class";
		sb.append(File.separatorChar + className);
		File classF = new File(sb.toString());
		cls = getClassInstance(name, new FileInputStream(classF), classF.length());
		return cls;
	}

	private Class<?> getClassInstance(String name,
			FileInputStream fileInputStream, long length) throws IOException {
		byte[] raw = new byte[(int) length];
		fileInputStream.read(raw);
		fileInputStream.close();
		return defineClass(name, raw, 0, raw.length);
	}
	
	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> cls = null;
		cls = findLoadedClass(name);
		if (!this.clazzes.contains(name) && cls == null) {
			cls = getSystemClassLoader().loadClass(name);
		}
		if (cls == null) {
			throw new ClassNotFoundException(name);
		}
		if (resolve) {
			resolveClass(cls);
		}
		return cls;
	}
}
