package com.baulsupp.groovy.groosh;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPerTaskExecutorService extends AbstractExecutorService {

	private boolean isShutdown = false;
	private Thread currentThread = null;
	
	
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
//TODO make this respond to timeout!
		
		if (currentThread == null)
			return true;
		if (!currentThread.isAlive())
			return true;
		currentThread.interrupt();
		return true;
	}

	public boolean isShutdown() {
		return isShutdown;
	}

	public boolean isTerminated() {
		if (currentThread == null)
			return true;
		if (!currentThread.isAlive())
			return true;
		return false;
	}

	public void shutdown() {
		isShutdown = true;
	}

	public List<Runnable> shutdownNow() {
		isShutdown = true;
		return null;
	}

	public void execute(Runnable command) {
		currentThread = new Thread(command);
		currentThread.start();
	}

}
