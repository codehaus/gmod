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

package org.codehaus.groovy.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class IOUtil {
	private static final int BUFFER_SIZE = 8192;

	private static int pump(InputStream is, OutputStream stream)
			throws IOException {
		int pumped = 0;
		byte[] buffy = new byte[BUFFER_SIZE];

		int read = 0;
		while ((read = is.read(buffy)) != -1) {
			stream.write(buffy, 0, read);
			pumped += read;
		}

		return pumped;
	}

	private static int pump(BlockingQueue<String> queue, OutputStream os)
			throws IOException, InterruptedException {

		int pumped = 0;
		Writer writer = new OutputStreamWriter(os);
		String line;
		while ((line = queue.take()) != null) {
			writer.write(line);
			pumped += line.length();
		}

		return pumped;
	}

	public static Future<Integer> pumpAsync(final InputStream is,
			final OutputStream os) {

		Future<Integer> result = getExecutor().submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				try {
					return pump(is, os);
				} finally {
					os.close();
					is.close();
				}
			}
		});

		return result;
	}

	public static ExecutorService getExecutor() {
		return new ThreadPerTaskExecutorService();
	}

	public static Future<Integer> pumpAsync(final BlockingQueue<String> queue,
			final OutputStream os) {
		Future<Integer> result = getExecutor().submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				try {
					return pump(queue, os);
				} finally {
					os.close();
				}
			}
		});

		return result;
	}

}
