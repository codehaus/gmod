//  Groosh -- Provides a shell-like capability for handling external processes
//
//  Copyright © 2007 Alexander Egger
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
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.process.Sink;
import org.codehaus.groovy.groosh.process.Source;


/**
 * Implements a basic 'cd' command which changes the base directory used to
 * execute processes. It is similar to the cd command of bash. Pipeing is
 * possible but has no effect.
 * 
 * @author Alexander Egger
 * 
 */
public class CdProcess extends GrooshProcess {

	public CdProcess(Object arg1, Map<String, String> env, ExecDir execDir)
			throws IOException {
		List<String> args = getArgs(arg1);
		String arg = args.get(0);

		execDir.setDir(new File(arg));

		if (env.get("PWD") != null) {
			env.put("OLDPWD", env.get("PWD"));
		}
		env.put("PWD", arg);
	}

	public void waitForExit() throws IOException {
		return;
	}

	public void start() throws IOException {

	}

	public Sink getSink() {
		return null;
	}

	public Source getSource() {
		return null;
	}
}
