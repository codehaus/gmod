package org.codehaus.groovy.groosh;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.codehaus.groovy.groosh.stream.Sink;
import org.codehaus.groovy.groosh.stream.Source;
import org.codehaus.groovy.util.IOUtil;

public abstract class AbstractBuiltInProcess extends GrooshProcess implements
		Callable<Boolean> {

	private int exitValue;
	private Source outSource;

	protected OutputStream os;

	private Future<Boolean> result;

	public static String[] aliases;

	public void waitForExit() throws IOException {
		try {
			result.get();
		} catch (Exception e) {
			exitValue = -1;
			throw new RuntimeException(e);
		}
	}

	public void startStreamHandling() throws IOException {
		os = System.out;
		result = IOUtil.getExecutor().submit(this);
	}

	public Sink getInput() {
		return null;
	}

	public Source getOutput() {
		if (outSource == null) {
			outSource = new OutSource();
		}
		return outSource;
	}

	public Source getError() {
		return null;
	}

	public class OutSource extends Source {
		public void connect(Sink sink) throws IOException {
			if (sink.providesOutputStream()) {
				AbstractBuiltInProcess.this.os = sink.getOutputStream();
			} else if (sink.receivesStream()) {
				Pipe pipe = Pipe.open();
				AbstractBuiltInProcess.this.os = Channels.newOutputStream(pipe
						.sink());
				sink.setInputStream(Channels.newInputStream(pipe.source()));
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public int exitValue() {
		return exitValue;
	}

}
