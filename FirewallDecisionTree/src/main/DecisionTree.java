package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecisionTree {

	private final static String REGEX1 = ";+\\”+\\’";
	private final static String REGEX2 = "(–.*)$";
	private final static String REGEX3 = "(/\\\\*).*(\\\\*/)";
	private final static String REGEX4 = "\"\\\"{2,}+";
	private final static String REGEX5 = "\"\\\\d=\\\\d\"";
	private final static String REGEX6 = "\"(\\\\s\\\\s)+\"";
	private final static String REGEX7 = "\"(#.*)$\"";
	private final static String REGEX8 = "\"%{2,}+\"";
	private final static String REGEX9 = "\"([;\\’\\\"\\\\=]+.*(admin.*))—((admin.*).*[;\\’\\\"\\\\=]+)\"";
	private final static String REGEX10 = "\"\\’{2,}+\"";
	private List<String> regexList;
	private Matcher matcher;
	private Pattern pattern;

	public DecisionTree() {
		regexList= Arrays.asList(new String [] {REGEX1,REGEX2,REGEX3,REGEX4,REGEX5,REGEX6,REGEX7,REGEX8,REGEX9,REGEX10});
	}

	public boolean evaluateString(String s) {
		for(String regex:regexList) {
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(s);
			if(matcher.find()) {
				return true;
			}
		}
		return false;
	}

	

}
