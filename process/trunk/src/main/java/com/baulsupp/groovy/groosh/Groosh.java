package com.baulsupp.groovy.groosh;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.baulsupp.process.IOUtil;

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
