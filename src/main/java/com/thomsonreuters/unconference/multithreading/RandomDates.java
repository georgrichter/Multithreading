package com.thomsonreuters.unconference.multithreading;

import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;
public class RandomDates {
	
	public void printDates(int number, Format dateFormatter) {
		
		System.out.println("Starting");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		List<Integer> list = new CopyOnWriteArrayList<>();
		
		Object[] resultArray = IntStream.range(1, 5)
				.parallel()
				.mapToObj(i -> String.format("%2d-Jun-2018", i))
				.map(t -> { for (int i = 1; i < 3000; i++) {list.add(i);}; return t;})
				.map(t -> dateFormat.parse(t, new ParsePosition(0)).getTime())
				.toArray();
		
		System.out.println(Arrays.toString(resultArray));
		System.out.println(list.size());

		
//		for (int i = 0; i < number; i++) {
//		
//			long l = (long) random.nextInt() + 1_520_000_000_000L;
//			String strDate = dateFormatter.format(l);
//			sb.append(strDate);
//			try {
//				dateFormatter.parseObject(strDate);
//			} catch (ParseException e) {}
//		}
//		
//		System.out.println(sb);
		System.out.println("Finished");
		
		
		
	}

}
