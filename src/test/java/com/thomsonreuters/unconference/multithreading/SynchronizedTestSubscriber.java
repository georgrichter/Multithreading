package com.thomsonreuters.unconference.multithreading;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.List;

import org.apache.log4j.Logger;

import com.thomsonreuters.unconference.multithreading.Subscriber;

public class SynchronizedTestSubscriber implements Subscriber {

	private static Logger LOG = Logger.getLogger(SynchronizedTestSubscriber.class);
	
	private List<String> receivedMessages = new CopyOnWriteArrayList<>();

	private CountDownLatch testBlockingLock;
	private CountDownLatch responseBlockingLock;

	public SynchronizedTestSubscriber(CountDownLatch testBlockingLock, CountDownLatch responseBlockingLock) {
		
		this.testBlockingLock = testBlockingLock;
		this.responseBlockingLock = responseBlockingLock;
	}

	@Override
	public void update(String response) {

		if (response.contains("blockMe")) {
			try {
				responseBlockingLock.await();
			} 
			catch (InterruptedException e) {}
		}else if (response.contains("releaseOther")) {
			testBlockingLock.countDown();
		}
		else {

			receivedMessages.add(response);
		}

	}
	
	public List<String> getReceivedMessages() {
		return receivedMessages;
	}

}
