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

import org.codehaus.groovy.groosh.sink.Sink;
import org.codehaus.groovy.groosh.sink.Source;

/**
 * Implements a basic 'cd' command which changes the base directory used to
 * execute processes. It is similar to the cd command of bash. Pipeing is
 * possible but has no effect.
 * 
 * The command processes only the first argument. Subsequent arguments are
 * ignored.
 * 
 * If the argument is a directory the execution path for the shell is set to
 * this directory. The environment variable PWD is set to this directory and the
 * environment variable OLDPWD is seth to the previous value of PWD
 * 
 * If the argument is "-" the execution path of the shell is set to OLDPWD if
 * this environment varibale exists. If not System.getProperty("user.home") is
 * used.
 * 
 * 
 * @author Alexander Egger
 * 
 */
public class CdProcess extends GrooshProcess {

	private static final String OLDPWD = "OLDPWD";
	private static final String PWD = "PWD";

	public CdProcess(List<String> args, Map<String, String> env, ExecDir execDir)
			throws IOException {
		String arg;
		if (args.isEmpty()) {
			arg = System.getProperty("user.dir");
		} else {
			arg = args.get(0);
			if (arg.equals("-"))
				arg = env.get(OLDPWD);
			if (arg == null) {
				arg = System.getProperty("user.home");
			}
		}

		File dir = new File(arg);
		if (!dir.exists()) {
			throw new IOException("Target directory " + arg + " ("
					+ dir.getAbsolutePath() + " ) " + "does not exist!");
		}

		if (!dir.isDirectory()) {
			throw new IOException("Target " + arg + " ("
					+ dir.getAbsolutePath() + " ) " + "is not a directory!");
		}
		execDir.setDir(dir);

		if (env.get(PWD) != null) {
			env.put(OLDPWD, env.get(PWD));
		} else {
			env.put(OLDPWD, System.getProperty("user.dir"));
		}
		env.put(PWD, arg);
	}

	public void waitForExit() throws IOException {
		return;
	}

	public void startStreamHandling() throws IOException {

	}

	public Sink getInput() {
		return null;
	}

	public Source getOutput() {
		return null;
	}

	public Source getError() {
		return null;
	}
}
