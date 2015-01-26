package com.geewaza.wangheng.study.jvm.datagulu03;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniJREPackageUtil {
	
	private File verboseLogFile;
	private File rtInputPath;
	private File charsetsInputPath;
	
	private String rtOutputPath;
	private String charsetsOutputPath;
	
	public MiniJREPackageUtil(String verboseLogFileString,
							   String rtInputPathString, 
							   String charsetsInputPathString, 
							   String rtOutputPathString, 
							   String charsetsOutputPathString) throws FileNotFoundException {
		verboseLogFile = new File(verboseLogFileString);
		rtInputPath = new File(rtInputPathString);
		charsetsInputPath = new File(charsetsInputPathString);
		if (!verboseLogFile.exists() || !rtInputPath.exists() || !charsetsInputPath.exists()) {
			throw new FileNotFoundException();
		}
		rtOutputPath = rtOutputPathString;
		charsetsOutputPath = charsetsOutputPathString;
		File rtOutputPathDir = new File(rtOutputPath);
		if (!rtOutputPathDir.exists()) {
			rtOutputPathDir.mkdirs();
		}
		File charsetsOutputPathDir = new File(charsetsOutputPath);
		if (!charsetsOutputPathDir.exists()) {
			charsetsOutputPathDir.mkdirs();
		}
	}
	
	/**
	 * 拷贝文件的工具方法
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean copyFile(String srcFileName, String destFileName) throws IOException {
		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		if (!srcFile.exists()) {
			throw new FileNotFoundException();
		}
		File destDir = new File(destFileName.substring(0, destFileName.lastIndexOf("/")));
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		destFile.createNewFile();
		byte buf[] = new byte[1024];
		FileInputStream fin = new FileInputStream(srcFile);
		FileOutputStream fout = new FileOutputStream(destFile);
		try {
			int len = 0;
			 while((len = fin.read(buf)) != -1) {
				 fout.write(buf,0,len);
			 }
			 fout.flush();
		} finally {
			fout.close();
			fin.close();
		}
		return true;
	}
	
	/**
	 * 拷贝class文件
	 * @throws IOException
	 */
	private void copyClasses() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(verboseLogFile));
		try {
			String line = null;
			while((line = reader.readLine()) != null) {
				analyzeLogLine(line);
			}
		} finally {
			reader.close();
		}
	}
	
	/**
	 * 分析verbose日志的每一行
	 * @param line
	 * @throws IOException
	 */
	private void analyzeLogLine(String line) throws IOException {
		if (line == null) {
			throw new NullPointerException();
		}
		String rex = "\\[Loaded ([\\w\\-\\$\\.\\\\]+) from ([\\w\\s\\.\\:\\\\\\/]+)\\]";
		Matcher matcher = Pattern.compile(rex).matcher(line);
		if (!matcher.matches()) {
			return;
		}
		String classFullName  = matcher.group(1);
		String libFullName = matcher.group(2);
		String classFileFullName = classFullName.replaceAll("\\.", "/");
		try {
			if (libFullName.endsWith("rt.jar")) {
				copyFile(rtInputPath + "/" + classFileFullName + ".class", rtOutputPath + "/" + classFileFullName + ".class");
			} else if (libFullName.endsWith("charsets.jar")) {
				copyFile(charsetsInputPath + "/" + classFileFullName + ".class", charsetsOutputPath + "/" + classFileFullName + ".class");
			} else {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("src = " + rtInputPath + "/" + classFileFullName + ".class" + "; dest = " +rtOutputPath + "/" + classFileFullName + ".class");
			
		}
	}

	public static void main(String[] args) throws IOException {
		String verboseLogFile = "D:\\opt\\wangheng\\input\\verbose.log";
		String rtInputPath = "D:\\opt\\wangheng\\input\\rt";
		String charsetsInputPath = "D:\\opt\\wangheng\\input\\charsets";
		
		String rtOutputPath = "D:\\opt\\wangheng\\output\\rt";
		String charsetsOutputPath = "D:\\opt\\wangheng\\output\\charsets";
		
		MiniJREPackageUtil util = new MiniJREPackageUtil(verboseLogFile, rtInputPath, charsetsInputPath, rtOutputPath, charsetsOutputPath);
		util.copyClasses();
	}

}
