//  !!projectname!! -- Provides a shell-like capability for handling external processes
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

import java.io.IOException;

import com.baulsupp.process.AppProcess;
import com.baulsupp.process.ProcessFactory;
import com.baulsupp.process.Sink;
import com.baulsupp.process.Source;

/**
 * 
 * @author Yuri Schimke
 *
 */
public class ShellProcess extends GrooshProcess {
	private AppProcess process = null;
	private String[] args;

	public ShellProcess(String name, Object arg1) throws IOException {
		this.args = getArgs(arg1);
		process = ProcessFactory.buildProcess(name, args);
	}

	private String[] getArgs(Object arg1) {
		if (arg1 == null)
			return new String[0];
		else if (arg1 instanceof String[])
			return (String[]) arg1;
		else if (arg1 instanceof Object[]) {
			Object[] argsO = (Object[]) arg1;
			String[] argsS = new String[argsO.length];
			for (int i = 0; i < argsO.length; i++) {
				argsS[i] = String.valueOf(argsO[i]);
			}
			return argsS;
		} else if (arg1 instanceof String)
			return new String[] { (String) arg1 };
		else
			throw new IllegalStateException("no support for args of type "
					+ arg1.getClass());
	}

	public void waitForExit() throws IOException {
		process.result();
	}

	public void start() throws IOException {
		process.start();
	}

	public Sink getSink() {
		return process.getInput();
	}

	public Source getSource() {
		return process.getOutput();
	}
}
