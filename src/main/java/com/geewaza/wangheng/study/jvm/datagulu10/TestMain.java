package com.geewaza.wangheng.study.jvm.datagulu10;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class TestMain {
	private static String classFile = "D:\\opt\\HelloWorld.class";
	
	public static void main(String[] args) throws IOException {
		test001();
	}

	private static void test001() throws IOException {
		RandomAccessFile fileInputStream = new RandomAccessFile(classFile, "r");
		
		try {
			System.out.println(0x00000044 - 0x00000010);
			System.out.println(getString(fileInputStream, 0x00000010, 52));
			System.out.println(getU2Int(fileInputStream, 0x00000006));
			System.out.println(getU1Int(fileInputStream, 0x0000000a));
		} finally {
			fileInputStream.close();
		}
		
		
		
	}
	
	public static String getString(RandomAccessFile raf, long pos, int length) throws IOException {
		byte[] byteArray = new byte[length];
		raf.seek(pos);
		raf.read(byteArray);
		String result = new String(byteArray, "utf-8");
		return result;
	}
	
	public static int getU2Int(RandomAccessFile raf, long pos) throws IOException {
		byte[] byteArray = new byte[2];
		raf.seek(pos);
		raf.read(byteArray);
		int result = byteArray[1] & 0xFF | (byteArray[0] & 0xFF) << 8;
		return result;
	}
	
	public static int getU1Int(RandomAccessFile raf, long pos) throws IOException {
		byte[] byteArray = new byte[1];
		raf.seek(pos);
		raf.read(byteArray);
		int result = byteArray[0];
		return result;
	}
	
	public static int getU4Int(RandomAccessFile raf, long pos) throws IOException {
		byte[] byteArray = new byte[4];
		raf.seek(pos);
		raf.read(byteArray);
		int result = (byteArray[0] & 0xff) | ((byteArray[1] << 8) & 0xff00) | ((byteArray[2] << 24) >>> 8) | (byteArray[3] << 24); 
		return result;
	}
	
}
