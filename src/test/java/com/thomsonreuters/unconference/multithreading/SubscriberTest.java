package com.thomsonreuters.unconference.multithreading;

import static org.junit.Assert.assertEquals;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class SubscriberTest {
	
//	@SuppressWarnings("deprecation")
//	@Rule
//	public Timeout globalTimeOut = new Timeout(5_000);
	
	private static Logger LOG = Logger.getLogger(SubscriberTest.class);
	
	@Test
	public void testPrioritisationOrderVersionOne() {
		
		// Generate unsorted list of random Strings
		List<String> unsortedList = StringProvider.getRandomStringList(10, 5);  
		
		// Create sorted copy of unsorted list
		List<String> sortedList = new ArrayList<>(unsortedList);
		Collections.sort(sortedList);

		// Send ten subscription requests to publisher, by looping through the unsorted list
		Publisher publisher = new Publisher();
		TestSubscriber subscriber = new TestSubscriber();
		
		for (String request : unsortedList) {
			LOG.info("Sending request " + request);
			publisher.subscribe(request,  subscriber);
		}
		
		// Verify that the received messages match the sorted list
		assertEquals(sortedList, subscriber.getReceivedMessages());
		
	}
	
	@Test
	public void testPrioritisationOrderVersionTwo() throws InterruptedException {
		
		CountDownLatch testBlockingLock = new CountDownLatch(1);
		CountDownLatch responseBlockingLock = new CountDownLatch(1);
		
		// Generate unsorted list of random Strings
		List<String> unsortedList = StringProvider.getRandomStringList(10, 5);  
		
		// Create sorted copy of unsorted list
		List<String> sortedList = new ArrayList<>(unsortedList);
		Collections.sort(sortedList);

		// Send blocking request to cause the publisher thread to wait
		Publisher publisher = new Publisher();
		SynchronizedTestSubscriber subscriber = new SynchronizedTestSubscriber(testBlockingLock, responseBlockingLock);
		publisher.subscribe("00000-blockMe", subscriber);
		
		
		// Send ten subscription requests to publisher, by looping through the unsorted list

		
		for (String request : unsortedList) {
			LOG.info("Sending request " + request);
			publisher.subscribe(request,  subscriber);
		}
		
		// Now send the "end of test" request
		publisher.subscribe("zzzz-releaseOther", subscriber);
		
		// Unblock the publisher thread
		responseBlockingLock.countDown();
		
		// Now this thread has to wait until the response thread completes all requests
		testBlockingLock.await();
		
		// Verify that the received messages match the sorted list
		assertEquals(sortedList, subscriber.getReceivedMessages());
		
	}
	
	@Test
	public void testReverseCommand() throws InterruptedException {
		
		CountDownLatch testBlockingLock = new CountDownLatch(1);
		CountDownLatch responseBlockingLock = new CountDownLatch(1);

		CountDownLatch appenderBlockingLock = new CountDownLatch(1);
		CountDownLatch thisBlockingLock = new CountDownLatch(1);
		
		Logger publisherLogger = Logger.getLogger(Publisher.class);
		publisherLogger.addAppender(new ThreadSynchronizationAppender(appenderBlockingLock, thisBlockingLock, 5));
		
		// Create list of requests (in order of submission) and second list with same elements in order of expected processing
		List<String> requests = StringProvider.getAlphaStringList(10,  5);
		List<String> expectedRequests = new ArrayList<>(requests);
		Collections.swap(expectedRequests, 5, 9);
		Collections.swap(expectedRequests, 6, 8);
		
		// Send blocking request to cause the publisher thread to wait
		Publisher publisher = new Publisher();
		SynchronizedTestSubscriber subscriber = new SynchronizedTestSubscriber(testBlockingLock, responseBlockingLock);
		publisher.subscribe("00000-blockMe", subscriber);
		
		
		// Send ten subscription requests to publisher, by looping through the unsorted list

		
		for (String request : requests) {
			LOG.debug("Sending request: " + request);
			publisher.subscribe(request,  subscriber);
		}
		
		// Now send the "end of test" request
		publisher.subscribe("FAAAA-releaseOther", subscriber);
		
		// Unblock the publisher thread
		responseBlockingLock.countDown();
		
		// Wait for the fifth response to be sent
		thisBlockingLock.await();
		// Send the reverse request
		publisher.subscribe("REVERSE-QUEUE",  null);
		// Unblock the publisher Thread
		appenderBlockingLock.countDown();
		
		// Now this thread has to wait until the response thread completes all requests
		testBlockingLock.await();
		
		// Verify that the received messages match the sorted list
		assertEquals(expectedRequests, subscriber.getReceivedMessages());
		
	}
	
	private class ThreadSynchronizationAppender extends AppenderSkeleton {

		private int callCount;
		private final int interruptCycle;
		private final CountDownLatch thisThreadBlocker;
		private final CountDownLatch otherThreadBlocker;
		
		ThreadSynchronizationAppender(CountDownLatch thisThreadBlocker, CountDownLatch otherThreadBlocker, int interruptCycle) {

			this.thisThreadBlocker = thisThreadBlocker;
			this.otherThreadBlocker = otherThreadBlocker;
			this.interruptCycle = interruptCycle;

		}

		
		@Override
		protected void append(LoggingEvent event) {
			
			if (callCount++ == interruptCycle) {
				
				otherThreadBlocker.countDown();
				try {
					thisThreadBlocker.await();
				} 
				catch (InterruptedException e) {}
			}
		}
		
		@Override
		public void close() {
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		
	}
	
	@Test
	public void testRandomDate() throws InterruptedException {
		
		RandomDates randomDates = new RandomDates();
		Format dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		
		for (int i = 0; i<200; i++) {
			randomDates.printDates(6, dateFormat);
		}
		

		
//		Thread[] threads = {
//				new Thread(new Runnable() {
//				public void run() {
//					System.out.println(Thread.currentThread().getName());
//					randomDates.printDates(10, dateFormat);
//				}})
//				new Thread(new Runnable() {
//				public void run() {
//					randomDates.printDates(10, dateFormat);
//				}}),
//				new Thread(new Runnable() {
//				public void run() {
//					randomDates.printDates(10, dateFormat);
//				}}),
//				new Thread(new Runnable() {
//				public void run() {
//					randomDates.printDates(10, dateFormat);
//				}}),
//				new Thread(new Runnable() {
//				public void run() {
//					randomDates.printDates(10, dateFormat);
//				}})
//		}; 
		
		
//		for (Thread thread : threads) {
//			
//			thread.start();
//			
//		}
//		
//		for (Thread thread : threads) {
//			
//			thread.join();
//			
//		}

	}
	
}
