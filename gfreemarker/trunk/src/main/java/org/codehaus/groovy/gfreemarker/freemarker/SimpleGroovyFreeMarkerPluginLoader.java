/*
 * Copyright 2003-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.codehaus.groovy.gfreemarker.freemarker;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A plugin loader which loads plugins from a directory
 * User: cedric
 * Date: 2 août 2007
 * Time: 19:46:46
 */
public class SimpleGroovyFreeMarkerPluginLoader implements IGroovyFreeMarkerPluginLoader {
	private String theGroovyScriptPath;
	private Map<String, IGroovyFreeMarkerPlugin> thePlugins;

	public SimpleGroovyFreeMarkerPluginLoader(String aGroovyScriptsPath) {
		theGroovyScriptPath = aGroovyScriptsPath;
		thePlugins = new HashMap<String, IGroovyFreeMarkerPlugin>();
	}

	public IGroovyFreeMarkerPlugin loadPlugin(String aPluginName) {
		IGroovyFreeMarkerPlugin toReturn = thePlugins.get(aPluginName);
		if (toReturn!=null) return toReturn;
		// try to load the script
		File pnFile = new File(theGroovyScriptPath, aPluginName+".groovy");
		GroovyClassLoader loader = new GroovyClassLoader();
		try {
			Class clazz = loader.parseClass(pnFile);
			IGroovyFreeMarkerPlugin plugin = (IGroovyFreeMarkerPlugin) clazz.newInstance();
			thePlugins.put(aPluginName, plugin);
			return plugin;
		} catch (IOException e) {
			throw new GroovyPluginLoadingError("Could not read plugin file " + aPluginName + ".groovy", e);
		} catch (IllegalAccessException e) {
			throw new GroovyPluginLoadingError("Could not create plugin " + aPluginName,e);
		} catch (InstantiationException e) {
			throw new GroovyPluginLoadingError("Could not create plugin " + aPluginName,e);
		} catch (ClassCastException e) {
			throw new GroovyPluginLoadingError("Plugin " + aPluginName + " is not a valid GroovyFreeMarker plugin",e);
		}
	}

	public static class GroovyPluginLoadingError extends RuntimeException {
		public GroovyPluginLoadingError() {
		}

		public GroovyPluginLoadingError(String message) {
			super(message);
		}

		public GroovyPluginLoadingError(String message, Throwable cause) {
			super(message, cause);
		}

		public GroovyPluginLoadingError(Throwable cause) {
			super(cause);
		}
	}
}
