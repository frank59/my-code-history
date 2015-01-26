package com.geewaza.wangheng.study.jvm.datagulu03.copy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 运行时使用参数 java -verbose VerboseTester > ./verbose/verbose.log
 * 
 * @author WangHeng
 *
 */
public class VerboseTester {
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("Hello");
		while (true) {
			TimeUnit.MILLISECONDS.sleep(1000);
		}
	}
}
