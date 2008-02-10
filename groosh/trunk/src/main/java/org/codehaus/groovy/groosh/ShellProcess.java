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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.process.AppProcess;
import org.codehaus.groovy.groosh.process.ProcessFactory;
import org.codehaus.groovy.groosh.sink.Sink;
import org.codehaus.groovy.groosh.sink.Source;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class ShellProcess extends GrooshProcess {
	private AppProcess process = null;

	public ShellProcess(List<String> command, Map<String, String> env,
			ExecDir execDir) throws IOException {
		process = ProcessFactory.buildProcess(command, env, execDir);
	}

	public void waitForExit() throws IOException {
		process.result();
	}

	public void startStreamHandling() throws IOException {
		process.start();
	}

	public Sink getInput() {
		return process.getInput();
	}

	public Source getOutput() {
		return process.getOutput();
	}

	public Source getError() {
		return process.getError();
	}

	public void destroy() {
		process.destroy();
	}
}
