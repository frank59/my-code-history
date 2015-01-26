package com.geewaza.wangheng.study.others.soku.trietree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class Tester {
	
	
	@Test
	public void test001() {
		String[] datas = {
				"小苹果",
				"筷子兄弟",
				"后会无期",
				"朴树",
				"太阳照常升起",
				"久石让",
				"让子弹飞",
				"yesterday once more",
				"undefind",
				"EXO",
				"yesterday",
		};
		
		String[] querys = {
				"yesterday",
				"yesterday once",
				"后会无期朴树",
		};
		
		ProgrammeTree<String> tree = new ProgrammeTree<String>();
		
		for (String key : datas) {
			boolean keepspcase = WordUtils.isEnglishWords(key);
			tree.insert(key, key, 0, keepspcase, false);
		}
		for (String query : querys) {
			List<String> searchResults = new ArrayList<String>();
			String result = null;
			System.out.println(query);
			do {
				if (result != null) {
					if (query.indexOf(result) + result.length() < query.length()) {
						query = query.substring(query.indexOf(result) + result.length(), query.length());
					} else {
						query = null;
					}
					searchResults.add(result);
				}
				if (!StringUtils.isBlank(query)) {
					result = tree.searchFastDeepFirst(query, WordUtils.isEnglishWords(query));
				}
			} while (result != null && !StringUtils.isBlank(query)) ;
			System.out.println(searchResults);
		}
		
	}
}
