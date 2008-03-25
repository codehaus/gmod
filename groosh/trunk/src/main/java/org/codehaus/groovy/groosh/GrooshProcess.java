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

import groovy.lang.Closure;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.codehaus.groovy.groosh.stream.DevNull;
import org.codehaus.groovy.groosh.stream.FileStreams;
import org.codehaus.groovy.groosh.stream.IOStreams;
import org.codehaus.groovy.groosh.stream.Sink;
import org.codehaus.groovy.groosh.stream.Source;
import org.codehaus.groovy.groosh.stream.StandardStreams;
import org.codehaus.groovy.groosh.stream.StringStreams;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

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

	public void toStream(OutputStream os) throws IOException {
		Sink sink = IOStreams.outputStreamSink(os);

		getOutput().connect(sink);
		startStreamHandling();

	}

	public GrooshProcess toFile(File f) throws IOException {
		Sink sink = FileStreams.fileSink(f, false);

		processSink(sink);
		return this;
	}

	public GrooshProcess toFile(String fn) throws IOException {
		return toFile(new File(fn));
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

	public GrooshProcess or(Sink sink) throws IOException {
		processSink(sink);
		return this;
	}

	// public GrooshProcess leftShift(String file) throws IOException {
	// return fromFile(file);
	// }

	public GrooshProcess leftShift(File file) throws IOException {
		return fromFile(file);
	}

	// public GrooshProcess rightShift(String file) throws IOException {
	// return toFile(file);
	// }

	public GrooshProcess rightShift(File file) throws IOException {
		return toFile(file);
	}

	public void toStdOut() throws IOException {
		Sink sink = StandardStreams.stdout();

		processSink(sink);
	}

	public void toDevNull() throws IOException {
		Sink sink = DevNull.devnull();

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

	public GrooshProcess fromFile(File file) throws IOException {
		Source source = FileStreams.fileSource(file);
		source.connect(getInput());
		return this;
	}

	public GrooshProcess fromFile(String file) throws IOException {
		Source source = FileStreams.fileSource(new File(file));
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

	public void eachLine(Closure closure) throws IOException {
		IOStreams.InputStreamSink sink = IOStreams.inputStreamSink();

		getOutput().connect(sink);
		startStreamHandling();
		DefaultGroovyMethods.eachLine(sink.getInputStream(), closure);
	}

	public List<String> toList() throws IOException {
		IOStreams.InputStreamSink sink = IOStreams.inputStreamSink();
		getOutput().connect(sink);
		startStreamHandling();
		BufferedReader ris = new BufferedReader(new InputStreamReader(sink
				.getInputStream()));
		List<String> result = new ArrayList<String>();
		String line;
		while ((line = ris.readLine()) != null) {
			result.add(line);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Object> grep(Object filter) throws IOException {
		return DefaultGroovyMethods.grep(toList(), filter);
	}

	public abstract int exitValue();

}
