//  Groosh -- Provides a shell-like capability for handling external processes
//
//  Copyright © 2007 Alexander Egger
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package com.baulsupp.groovy.groosh;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Alexander Egger
 * 
 */
public class ThreadPerTaskExecutorService extends AbstractExecutorService {

	private boolean isShutdown = false;
	private Thread currentThread = null;

	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO make this respond to timeout!

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
