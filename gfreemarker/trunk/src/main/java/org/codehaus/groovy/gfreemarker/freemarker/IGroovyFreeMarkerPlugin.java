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

import java.util.Map;

/**
 * The interface the Groovy FreeMarker plugins should implement.
 * User: cedric
 * Date: 2 août 2007
 * Time: 19:47:50
 */
public interface IGroovyFreeMarkerPlugin {
	/**
	 * Transforms text content thanks to a groovy script
	 * @param someParams a map of parameters provided by the template
	 * @param aContent the content of the text to transform
	 * @return &lt;code>String&lt;/code> the transformed text
	 */
	String transform(Map someParams, String aContent);
}
