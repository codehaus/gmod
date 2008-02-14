//  Groosh -- Provides a shell-like capability for handling external processes
//
//  Copyright © 2004 Yuri Schimke
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

package org.codehaus.groovy.groosh.stream;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * 
 * @author Yuri Schimke
 * @author Alexander Egger
 * 
 */
public abstract class Source {
	protected Future<Integer> streamPumpResult;

	public abstract void connect(Sink sink) throws IOException;

	/**
	 * The input stream is pumped to the output stream asynchronously. Call this
	 * method to wait until the output stream and input stream are fully
	 * processed and closed.
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void waitForStreamsHandled() throws InterruptedException,
			ExecutionException {
		if (streamPumpResult == null) {
			throw new NullPointerException("streamPumpResult must not be null");
		} else {
			streamPumpResult.get();
		}

	}
}
