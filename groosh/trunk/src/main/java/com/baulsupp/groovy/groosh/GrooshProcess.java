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

package com.baulsupp.groovy.groosh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baulsupp.process.FileStreams;
import com.baulsupp.process.Sink;
import com.baulsupp.process.Source;
import com.baulsupp.process.StandardStreams;
import com.baulsupp.process.StringStreams;

// TODO class should not be reentrant 
// that is if output is already set, don't let it be done twice.
/**
 * 
 * @author Yuri Schimke
 * 
 */
public abstract class GrooshProcess {
	protected abstract Sink getSink();

	protected abstract Source getSource();

	public String toStringOut() throws IOException {
		StringStreams.StringSink sink = StringStreams.stringSink();

		getSource().connect(sink);
		start();

		return sink.toString();
	}

	// TODO should this be asynchronous, would be less obvious though!
	public void toFile(File f) throws IOException {
		Sink sink = FileStreams.sink(f, false);

		getSource().connect(sink);
		start();
		waitForExit();
	}

	// needs to be asynchronous so they can continue the chain
	public GrooshProcess pipeTo(GrooshProcess process) throws IOException {
		getSource().connect(process.getSink());

		start();

		// return other process so chaining is possible
		return process;
	}

	// TODO should this be asynchronous, would be less obvious though!
	public void toStdOut() throws IOException {
		Sink sink = StandardStreams.stdout();

		getSource().connect(sink);
		start();

		waitForExit();
	}

	public GrooshProcess fromStdIn() throws IOException {
		Source source = StandardStreams.stdin();

		source.connect(getSink());

		return this;
	}

	public GrooshProcess fromString(String s) throws IOException {
		Source source = StringStreams.stringSource(s);

		source.connect(getSink());

		return this;
	}

	protected List<String> getArgs(Object arg1) {
		if (arg1 == null)
			return new ArrayList<String>();
		else if (arg1 instanceof String[])
			return Arrays.asList((String[]) arg1);
		else if (arg1 instanceof Object[]) {
			List<String> retval = new ArrayList<String>();
			Object[] argsO = (Object[]) arg1;
			for (Object object : argsO) {
				retval.add(String.valueOf(object));
			}
			return retval;
		} else if (arg1 instanceof String) {
			List<String> retval = new ArrayList<String>();
			retval.add((String) arg1);
			return retval;
		} else
			throw new IllegalStateException("no support for args of type "
					+ arg1.getClass());
	}

	public abstract void start() throws IOException;

	public abstract void waitForExit() throws IOException;
}
