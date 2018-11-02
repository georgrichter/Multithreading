package com.thomsonreuters.unconference.multithreading;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.List;

import org.apache.log4j.Logger;

import com.thomsonreuters.unconference.multithreading.Subscriber;

public class TestSubscriber implements Subscriber {

	private static Logger LOG = Logger.getLogger(TestSubscriber.class);
	
	private List<String> receivedMessages = new CopyOnWriteArrayList<>();

	@Override
	public void update(String response) {

		receivedMessages.add(response);

	}
	
	public List<String> getReceivedMessages() {
		return receivedMessages;
	}

}
