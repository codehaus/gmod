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

package org.codehaus.groovy.groosh.process;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.ExecDir;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class JavaProcess implements AppProcess {
	private Process process;

	private boolean errHandled = false;
	private boolean outHandled = false;
	private boolean inHandled = false;

	private JavaProcess(List<String> command, Map<String, String> env,
			ExecDir execDir) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);

		Map<String, String> currentEnv = builder.environment();
		currentEnv.putAll(env);
		builder.directory(execDir.getDir());
		process = builder.start();
	}

	public static JavaProcess createProcess(List<String> command,
			Map<String, String> env, ExecDir execDir) throws IOException {

		return new JavaProcess(command, env, execDir);
	}

	public void start() throws IOException {
		if (!inHandled) {
			process.getOutputStream().close();
		}

		// Should we throw away, it would make it explicit to direct the output
		// somewhere.
		if (!outHandled)
			IOUtil.pumpAsync(process.getInputStream(), StandardStreams.stderr()
					.getStream());

		if (!errHandled)
			IOUtil.pumpAsync(process.getErrorStream(), StandardStreams.stderr()
					.getStream());
	}

	public Sink getInput() {
		return new InSink();
	}

	public Source getOutput() {
		return new OutSource();
	}

	public Source getError() {
		return new ErrSource();
	}

	public int result() {
		try {
			return process.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hadError() {
		return result() != 0;
	}

	public class InSink extends Sink {
		public OutputStream getStream() {
			inHandled = true;
			return process.getOutputStream();
		}

		public boolean providesStream() {
			return true;
		}
	}

	public class OutSource extends Source {
		public void connect(Sink sink) {
			if (sink.providesStream()) {
				outHandled = true;
				// TODO handle result
				IOUtil.pumpAsync(process.getInputStream(), sink.getStream());
			} else if (sink.receivesStream()) {
				outHandled = true;
				sink.setStream(process.getInputStream());
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public class ErrSource extends Source {
		public void connect(Sink sink) {
			if (sink.providesStream()) {
				errHandled = true;
				// TODO handle result
				IOUtil.pumpAsync(process.getInputStream(), sink.getStream());
			} else if (sink.receivesStream()) {
				errHandled = true;
				sink.setStream(process.getInputStream());
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public void destroy() {
		process.destroy();
	}
}
