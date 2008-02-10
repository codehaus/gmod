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

package org.codehaus.groovy.groosh;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import org.codehaus.groovy.groosh.sink.DevNull;
import org.codehaus.groovy.groosh.sink.FileStreams;
import org.codehaus.groovy.groosh.sink.IOStreams;
import org.codehaus.groovy.groosh.sink.Sink;
import org.codehaus.groovy.groosh.sink.Source;
import org.codehaus.groovy.groosh.sink.StandardStreams;
import org.codehaus.groovy.groosh.sink.StringStreams;

// TODO class should not be reentrant 
// that is if output is already set, don't let it be done twice.
/**
 * 
 * @author Yuri Schimke
 * 
 */
public abstract class GrooshProcess {

	protected abstract Sink getInput();

	protected abstract Source getOutput();

	protected abstract Source getError();

	public abstract void startStreamHandling() throws IOException;

	public abstract void waitForExit() throws IOException;

	public String getText() throws IOException {
		StringStreams.StringSink sink = StringStreams.stringSink();

		getOutput().connect(sink);
		startStreamHandling();

		return sink.toString();
	}

	public String toStream(OutputStream os) throws IOException {
		Sink sink = IOStreams.outputStreamSource(os);

		getOutput().connect(sink);
		startStreamHandling();

		return sink.toString();
	}

	public void toFile(File f) throws IOException {
		Sink sink = FileStreams.fileSink(f, false);

		processSink(sink);
	}

	public void toFile(String fn) throws IOException {
		toFile(new File(fn));
	}

	public GrooshProcess pipeTo(GrooshProcess process) throws IOException {
		getOutput().connect(process.getInput());

		startStreamHandling();

		// return other process so chaining is possible
		return process;
	}

	public GrooshProcess or(GrooshProcess process) throws IOException {
		return pipeTo(process);
	}

	public void toStdOut() throws IOException {
		Sink sink = StandardStreams.stdout();

		processSink(sink);
	}

	public void toDevNull() throws IOException {
		Sink sink = DevNull.devNullSink();

		processSink(sink);
	}

	private void processSink(Sink sink) throws IOException {
		getOutput().connect(sink);
		startStreamHandling();
		waitForExit();

	}

	public GrooshProcess fromStdIn() throws IOException {
		Source source = StandardStreams.stdin();

		source.connect(getInput());

		return this;
	}

	public GrooshProcess fromString(String s) throws IOException {
		Source source = StringStreams.stringSource(s);

		source.connect(getInput());

		return this;
	}

	public void waitFor() throws InterruptedException, ExecutionException {
		getOutput().waitForStreamsHandled();
	}

}
