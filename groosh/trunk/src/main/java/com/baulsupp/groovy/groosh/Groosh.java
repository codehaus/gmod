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

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.baulsupp.process.IOUtil;

/**
 * 
 * @author Yuri Schimke
 *
 */
public class Groosh extends GroovyObjectSupport {
	private static Map<String, Class<? extends GrooshProcess>> registeredProcesses = 
		new HashMap<String, Class<? extends GrooshProcess>>();

	static {
		registerProcess("groovy", StreamClosureProcess.class);
		registerProcess("each_line", LineClosureProcess.class);
		registerProcess("grid", GridClosureProcess.class);
	}

	public static void registerProcess(String name,
			Class<? extends GrooshProcess> clazz) {
		registeredProcesses.put(name, clazz);
	}

	public Object invokeMethod(String name, Object args) {
		GrooshProcess process;
		//gsh.grep results in Groovies grep methode to be called 
		//not into the shell command created. Adding _ as a prefix
		//tells us that we mean the shell command.
		
		if (name.startsWith("_")) {
			name = name.substring(1);
		}
		try {
			if (registeredProcesses.containsKey(name)) {
				process = createProcess(registeredProcesses.get(name), args);
			} else {
				process = new ShellProcess(name, args);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	private GrooshProcess createProcess(Class<? extends GrooshProcess> class1, Object arg) {
		GrooshProcess process = null;

		try {
			Constructor<? extends GrooshProcess> c = class1
					.getConstructor(new Class[] { Closure.class });
			process = c.newInstance((Object[]) arg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	public void execute(Runnable r) {
		IOUtil.getExecutor().execute(r);
	}
}
