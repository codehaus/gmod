package com.baulsupp.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.baulsupp.groovy.groosh.ThreadPerTaskExecutorService;

public class IOUtil {
	private static final int BUFFER_SIZE = 8192;

	public static int pump(InputStream is, OutputStream stream)
			throws IOException {
		int pumped = 0;
		byte[] buffy = new byte[BUFFER_SIZE];

		int read = 0;
		while ((read = is.read(buffy)) != -1) {
			stream.write(buffy, 0, read);
			pumped += read;
		}

		return read;
	}

	public static Future<Integer> pumpAsync(final InputStream is,
			final OutputStream os) {

		Future<Integer> result = getExecutor().submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				try {
					return IOUtil.pump(is, os);
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
	
}
