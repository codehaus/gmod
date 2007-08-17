package com.baulsupp.groovy.groosh;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.baulsupp.process.IOUtil;
import com.baulsupp.process.Sink;
import com.baulsupp.process.Source;
import com.baulsupp.process.StandardStreams;

public class StreamClosureProcess extends GrooshProcess implements
		Callable<Boolean> {
	protected Closure closure;
	private InputStream is;
	private OutputStream os;
	private Future<Boolean> result;

	public StreamClosureProcess(Closure closure) {
		this.closure = closure;
	}

	public void start() {
		if (is == null)
			throw new RuntimeException("closure processes need a source");

		if (os == null)
			os = StandardStreams.stdout().getStream();

		result = IOUtil.getExecutor().submit(this);
	}

	public void waitForExit() throws IOException {
		try {
			result.get();
		} catch (Exception e) {
			// TODO handle the exceptions
			throw new RuntimeException(e);
		}
	}

	public Boolean call() throws Exception {
		try {
			process(is, os);
			return true;
		} finally {
			os.flush();
			os.close();
			is.close();
		}

	}

	protected void process(final InputStream is, final OutputStream os)
			throws IOException {
		List<Object> l = new ArrayList<Object>();
		l.add(is);
		l.add(os);
		closure.call(l);
		os.flush();
	}

	public class ClosureSink extends Sink {

		public void setStream(InputStream is) {
			StreamClosureProcess.this.is = is;
		}

		public boolean receivesStream() {
			return true;
		}
	}

	protected Sink getSink() {
		return new ClosureSink();
	}

	public class ClosureSource extends Source {
		public void connect(Sink sink) throws IOException {
			if (sink.providesStream()) {
				StreamClosureProcess.this.os = sink.getStream();
			} else if (sink.receivesStream()) {
				Pipe pipe = Pipe.open();
				StreamClosureProcess.this.os = Channels.newOutputStream(pipe
						.sink());
				sink.setStream(Channels.newInputStream(pipe.source()));
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	protected Source getSource() {
		return new ClosureSource();
	}
}
