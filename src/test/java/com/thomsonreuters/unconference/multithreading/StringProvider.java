package com.thomsonreuters.unconference.multithreading;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class StringProvider {
	
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String randomAlphaNumeric(int length) {

		StringBuilder builder = new StringBuilder();
		while (length-- != 0) {
		
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		
		}

	return builder.toString();

	}
	
	public static List<String> getRandomStringList(int count, int length) {
		
		List<String> stringList = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			stringList.add(randomAlphaNumeric(length));
		}
		
		return stringList;
		
	}
	
	public static List<String> getAlphaStringList(int count, int length) {
		
		List<String> stringList = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			char ch = (char) (i + (int) 'A');
			char[] chArray = new char[length];
			Arrays.fill(chArray, ch);
			stringList.add(new String(chArray));
		}
		
		return stringList;
		
	}
}
