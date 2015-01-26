package com.geewaza.wangheng.study.jvm.datagulu08;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SessionTimeMain {
	
	private static String log_file = "D:\\workspace\\study-project\\src\\main\\java\\com\\geewaza\\wangheng\\study\\jvm\\datagulu08\\result.txt";
	
	public static void main(String[] args) throws IOException {
		test001();
	}

	private static void test001() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(log_file));
		TreeMap<Long, List<String>> sortedTreeMap = new TreeMap<Long, List<String>>();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				long time = Long.parseLong(line);
				List<String> records = sortedTreeMap.get(time);
				if (records == null) {
					records = new ArrayList<String>();
					sortedTreeMap.put(time, records);
				}
				records.add(line);
			}
		} finally {
			reader.close();
		}
		
		int maxCount = 0;
		long maxStartTime = 0;
		for (Long time : sortedTreeMap.keySet()) {
			Map<Long, List<String>> subMap = sortedTreeMap.subMap(time, time + 1000L);
			int count = 0;
			for (List<String> list : subMap.values()) {
				count += list.size();
			}
			if (count > maxCount) {
				maxCount = count;
				maxStartTime = time;
			}
		}
		
		System.out.println("maxCount = " + maxCount + "; maxStartTime = " + maxStartTime);
	}

}
