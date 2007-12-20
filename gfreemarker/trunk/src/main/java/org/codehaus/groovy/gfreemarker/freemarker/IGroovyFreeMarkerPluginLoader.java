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

/**
 * The interface plugin loaders should implement.
 * User: cedric
 * Date: 3 août 2007
 * Time: 09:35:14
 */
public interface IGroovyFreeMarkerPluginLoader {
	/**
	 * Loads a plugin.
	 * @param aPluginName plugin to be loaded
	 * @return &lt;code>IGroovyFreeMarkerPlugin&lt;/code> a plugin instance
	 */
	IGroovyFreeMarkerPlugin loadPlugin(String aPluginName);
}
