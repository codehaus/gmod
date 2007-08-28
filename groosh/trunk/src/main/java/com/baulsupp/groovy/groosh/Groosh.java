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

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.baulsupp.process.IOUtil;

/**
 * The actual shell. Objects of this class are responsible to execute processes
 * and manage the environment
 * 
 * 
 * @author Yuri Schimke
 * @author Alexander Egger
 * 
 */
public class Groosh extends GroovyObjectSupport {
	private static Map<String, Class<? extends StreamClosureProcess>> registeredStreamClosureProcesses = new HashMap<String, Class<? extends StreamClosureProcess>>();

	private static Map<String, Class<? extends GrooshProcess>> registeredInternalProcesses = new HashMap<String, Class<? extends GrooshProcess>>();

	private Map<String, String> env = new HashMap<String, String>();

	private ExecDir execDir = new ExecDir();

	static {
		registerStreamClosureProcess("groovy", StreamClosureProcess.class);
		registerStreamClosureProcess("each_line", LineClosureProcess.class);
		registerStreamClosureProcess("grid", GridClosureProcess.class);
	}

	public static void registerStreamClosureProcess(String name,
			Class<? extends StreamClosureProcess> clazz) {
		registeredStreamClosureProcesses.put(name, clazz);
	}

	static {
		registerInternalProcess("cd", CdProcess.class);
	}

	public Groosh() {
		execDir.setDir(new File(System.getProperty("user.dir")));
	}

	public static void registerInternalProcess(String name,
			Class<? extends GrooshProcess> clazz) {
		registeredInternalProcesses.put(name, clazz);
	}

	public Object invokeMethod(String name, Object args) {
		GrooshProcess process;
		// gsh.grep results in Groovies grep methode to be called
		// not into the shell command created. Adding _ as a prefix
		// tells us that we mean the shell command.

		if (name.startsWith("_")) {
			name = name.substring(1);
		}
		try {
			if (registeredStreamClosureProcesses.containsKey(name)) {
				process = createStreamClosureProcess(
						registeredStreamClosureProcesses.get(name), args);
			}
			if (registeredInternalProcesses.containsKey(name)) {
				process = createInternalProcess(registeredInternalProcesses
						.get(name), args);
			} else {
				process = new ShellProcess(name, args, env, execDir);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	private GrooshProcess createInternalProcess(
			Class<? extends GrooshProcess> class1, Object args) {
		GrooshProcess process = null;

		try {
			Constructor<? extends GrooshProcess> c = class1
					.getConstructor(new Class[] { Object.class, Map.class,
							ExecDir.class });
			process = c.newInstance((Object[]) args, env, execDir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	private StreamClosureProcess createStreamClosureProcess(
			Class<? extends StreamClosureProcess> class1, Object args) {
		StreamClosureProcess process = null;

		try {
			Constructor<? extends StreamClosureProcess> c = class1
					.getConstructor(new Class[] { Closure.class });
			process = c.newInstance((Object[]) args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	public void execute(Runnable r) {
		IOUtil.getExecutor().execute(r);
	}

	public File getCurrentExecDir() {
		return execDir.getDir();
	}
}
