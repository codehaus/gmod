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

package groosh;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.CdProcess;
import org.codehaus.groovy.groosh.ExecDir;
import org.codehaus.groovy.groosh.GridClosureProcess;
import org.codehaus.groovy.groosh.GrooshProcess;
import org.codehaus.groovy.groosh.LineClosureProcess;
import org.codehaus.groovy.groosh.ShellProcess;
import org.codehaus.groovy.groosh.StreamClosureProcess;
import org.codehaus.groovy.groosh.process.IOUtil;

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

	private String sudoUser;
	private String sudoPassword = "";

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
		env.put("PWD", System.getProperty("user.dir"));
	}

	public static void registerInternalProcess(String name,
			Class<? extends GrooshProcess> clazz) {
		registeredInternalProcesses.put(name, clazz);
	}

	public Object invokeMethod(String name, Object args) {
		GrooshProcess process;
		boolean withSudo = false;

		if (name.startsWith("sudo_")) {
			name = name.substring("sudo_".length());
			withSudo = true;
		}
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
			} else if (registeredInternalProcesses.containsKey(name)) {
				process = createInternalProcess(registeredInternalProcesses
						.get(name), getArgs(args));
			} else {
				List<String> command;
				if (!withSudo) {
					command = getArgs(args);
					command.add(0, name);
				} else {
					command = new ArrayList<String>();
					command.add("sudo");
					command.add("-u");
					command.add(sudoUser);
					command.add("-S");
					command.add(name);
					command.addAll(getArgs(args));
				}
				// System.out.println(listAsString(command));
				process = new ShellProcess(command, env, execDir);
				if (withSudo) {
					process.fromString(sudoPassword);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	// private String listAsString(List<String> args) {
	// StringBuilder str = new StringBuilder();
	// for (String string : args) {
	// str.append(string);
	// str.append(" ");
	// }
	// return str.toString();
	// }

	private GrooshProcess createInternalProcess(
			Class<? extends GrooshProcess> class1, List<String> args) {
		GrooshProcess process = null;

		try {
			Constructor<? extends GrooshProcess> c = class1
					.getConstructor(new Class[] { List.class, Map.class,
							ExecDir.class });
			process = c.newInstance(args, env, execDir);
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

	public Map<String, String> getCurrentEnvironment() {
		return env;
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

	public void setSudoUser(String sudoUser) {
		this.sudoUser = sudoUser;
	}

	public void setSudoPassword(String sudoPassword) {
		this.sudoPassword = sudoPassword;
	}

}
