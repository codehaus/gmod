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
import java.util.jar.JarFile
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.AbstractBuiltInProcess;
import org.codehaus.groovy.groosh.Aliases;
import org.codehaus.groovy.groosh.GridClosureProcess;
import org.codehaus.groovy.groosh.GrooshProcess;
import org.codehaus.groovy.groosh.JavaProcess;
import org.codehaus.groovy.groosh.LineClosureProcess;
import org.codehaus.groovy.groosh.StreamClosureProcess;
import org.codehaus.groovy.util.ExecDir;
import org.codehaus.groovy.util.IOUtil;

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
	private static String BUILT_IN_PACKAGE = "org/codehaus/groovy/groosh/builtin"
	
	
	private static Map<String, Class<? extends StreamClosureProcess>> registeredStreamClosureProcesses = new HashMap<String, Class<? extends StreamClosureProcess>>();

	private static Map<String, Class<? extends GrooshProcess>> registeredInternalProcesses = new HashMap<String, Class<? extends GrooshProcess>>();

	private Map<String, String> env = new HashMap<String, String>();

	private static Map instances = Collections.synchronizedMap([:])

	
	private ExecDir execDir = new ExecDir();

	private String sudoUser;
	private String sudoPassword = "";

	static {
		registerStreamClosureProcess("groovy", StreamClosureProcess.class);
		registerStreamClosureProcess("each_line", LineClosureProcess.class);
		registerStreamClosureProcess("grid", GridClosureProcess.class);
	}

	private static void registerStreamClosureProcess(String name,
			Class<? extends StreamClosureProcess> clazz) {
		registeredStreamClosureProcesses.put(name, clazz);
	}

	static {
		registerInternalProcesses();
	}

	public static Groosh groosh() {
		return new Groosh();
	}

	public Groosh() {
		execDir.setDir(new File(System.getProperty("user.dir")));
		env.put("PWD", System.getProperty("user.dir"));
	}

	public static void registerInternalProcesses()
			throws ClassNotFoundException, IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {

		
		URL url = Groosh.class.classLoader.getResource(BUILT_IN_PACKAGE)
		File directory = new File(url.getFile());
		
		String[] files
		def classes = []
		if (url.protocol == "jar") {
			String name = directory
			name = name.replace("file:","")
			name = name.replace("!/$BUILT_IN_PACKAGE","")
			JarFile jar = new JarFile(new File(name))
			jar.entries().grep(~/org\/codehaus\/groovy\/groosh\/builtin\/.+/).each {
				classes.add(it.toString().replace(".class", "").replace("/","."))
			}
			
		} else {
			files = directory.list();
			for (String file : files) {
				if (file.endsWith(".class")) {
					// removes the .class extension
					String classname = file.replace(".class" ,"");
					classes.add("$BUILT_IN_PACKAGE.".replace("/",".")
							+ classname)
				}
			}
		}
		classes.each {
			Class<AbstractBuiltInProcess> clazz = (Class<AbstractBuiltInProcess>) Class
				.forName(it);
			Aliases alias = clazz.getAnnotation(Aliases.class);
			String[] aliases = alias.value();
			for (String name : aliases) {
				registeredInternalProcesses.put(name, clazz);
			}
		}
	}

	public Object invokeMethod(String name, Object args) {
		GrooshProcess process;
		boolean withSudo = false;

		if (name.startsWith("sudo_")) {
			name = name.substring("sudo_".length());
			withSudo = true;
		}
		// gsh.grep results in Groovies grep method to be called
		// not into the shell command created. Adding _ as a prefix
		// tells us that we mean the shell command.
		if (name.startsWith("_")) {
			name = name.substring(1);
		}

		name = name.replaceAll("___", "-");

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
				process = new JavaProcess(command, env, execDir);
				if (withSudo) {
					process.fromString(sudoPassword);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return process;
	}

	private GrooshProcess createInternalProcess(
			Class<? extends GrooshProcess> class1, List<String> args) {
		GrooshProcess process = null;

		try {
			Class[] params = new Class[3];
			params[0] = List.class;
			params[1] = Map.class;
			params[2] = ExecDir.class;
			
			Constructor<? extends GrooshProcess> c = class1
					.getConstructor(params);
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
			Class[] params = new Class[1];
			params[0] = Closure.class;
			Constructor<? extends StreamClosureProcess> c = class1
					.getConstructor(params);
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
	
	static void withGroosh(Object it) {
		def emc = new ExpandoMetaClass( it.class, false )
		emc.invokeMethod = { String name, args ->
			def metaMethod = delegate.class.metaClass.getMetaMethod(name, args)
			def result 
			if (metaMethod) {
				result = metaMethod.invoke(delegate,args)
			} else {
				result = groosh.invokeMethod(name,args)
			}
			result
		}
		emc.getStdout = { -> org.codehaus.groovy.groosh.stream.StandardStreams.stdout() }
		emc.getDevnull = { -> org.codehaus.groovy.groosh.stream.DevNull.devnull()}

		emc.groosh = new Groosh()  
		emc.initialize()
		it.metaClass = emc
	
	}

}
