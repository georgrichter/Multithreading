package com.thomsonreuters.unconference.multithreading;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

public class Publisher {
	
	private BlockingQueue<SubscriptionRequest> ascendingRequestQueue = new PriorityBlockingQueue<>();
	private BlockingQueue<SubscriptionRequest> descendingRequestQueue = new PriorityBlockingQueue<>(10, new SubscriptionRequestInverseComparator());
	private BlockingQueue<SubscriptionRequest> requestQueue = ascendingRequestQueue;

	private static Logger LOG = Logger.getLogger(Publisher.class);

	
	public Publisher() {
		
		Thread processThread = new Thread() {
			
			@Override
			public void run() {
				
				while(true) {

					try {

						synchronized(this) {
							SubscriptionRequest subscriptionRequest = requestQueue.take();
							LOG.info("Sending response " + subscriptionRequest.getRequest());
							subscriptionRequest.getSubscriber().update(subscriptionRequest.getRequest());
						}

					} catch (InterruptedException e) {}
				}
			}
		};
		
		processThread.setDaemon(true);
		processThread.start();
		
	}
	
	public void subscribe(String request, Subscriber subscriber) {
		
		if (request.equals("REVERSE-QUEUE")) {
			synchronized(this) {
				switchQueueOrder();
			}
		}
		else {

			try {
				requestQueue.put(new SubscriptionRequest(subscriber, request));
			} catch (InterruptedException e) {}

		}
	}
	
	private void switchQueueOrder() {
		
		if (requestQueue == ascendingRequestQueue) {
			ascendingRequestQueue.drainTo(descendingRequestQueue);
			requestQueue = descendingRequestQueue;
		}
		else {
			descendingRequestQueue.drainTo(ascendingRequestQueue);
			requestQueue = ascendingRequestQueue;
		}
	}
	
	private class SubscriptionRequest implements Comparable<SubscriptionRequest> {
		
		final Subscriber subscriber;
		final String request;
	
		public SubscriptionRequest(Subscriber subscriber, String request) {
			this.subscriber = subscriber;
			this.request = request;
		}
		
		public Subscriber getSubscriber() {
			return subscriber;
		}
		public String getRequest() {
			return request;
		}
		
		public int compareTo(SubscriptionRequest otherRequest) {
			return request.compareTo(otherRequest.request);
		}
	}
	
	private class SubscriptionRequestInverseComparator implements Comparator<SubscriptionRequest> {

		@Override
		public int compare(SubscriptionRequest request1, SubscriptionRequest request2) {
			return request2.compareTo(request1);
		}
	}
}
