package com.geewaza.wangheng.study.jvm.datagulu03.copy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class VerboseLogAnalyzer {
	
	private File logFile;
	
	public VerboseLogAnalyzer(String fileName) throws FileNotFoundException {
		logFile = new File(fileName);
		if (!logFile.exists()) {
			throw new FileNotFoundException("File " + fileName + " dosen't exists!");
		}
	}
	
}
