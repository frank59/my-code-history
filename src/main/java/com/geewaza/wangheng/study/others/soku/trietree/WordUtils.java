package com.geewaza.wangheng.study.others.soku.trietree;

public class WordUtils {
	
	private static char[] numberWords = new char[]{
		'0','1','2','3','4','5','6','7','8','9','一','二','三','四','五','六','七','八','九','十'
	};
	
	private static final String CHINES_NUMBER = "零|一|二|三|四|五|六|七|八|九|十|百|千|万|亿|兆";
	
	private static final String EN_ROMAN_NUM = "I|i|II|ii|III|iii|IV|iv|V|v|VI|vi|VII|vii|VIII|viii|IX|ix|X|x|XI|xi|XII|xii|XIII|xiii";
	
	/**
	 * 是否只包含英语单词 
	 * @param word
	 * @return
	 */
	public static boolean isEnglishWords(String word) {
		String regex="^[[\\w\\p{Punct}]+[ \t]+]*[\\w\\p{Punct}]+(\\s*第(" + CHINES_NUMBER + "+)[季|部|期|集])?$";
		return word.matches(regex);
	}

}
