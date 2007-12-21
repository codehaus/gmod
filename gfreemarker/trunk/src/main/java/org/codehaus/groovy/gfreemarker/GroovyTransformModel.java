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

package org.codehaus.groovy.gfreemarker;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.DeepUnwrap;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * A transform which allows calling Groovy scripts (plugins) in a FreeMarker template.
 * User: cedric
 * Date: 2 août 2007
 * Time: 19:33:43
 */
public class GroovyTransformModel implements TemplateTransformModel {
	private IGroovyFreeMarkerPluginLoader theLoader;

	public GroovyTransformModel(IGroovyFreeMarkerPluginLoader aLoader) {
		theLoader = aLoader;
	}

	public Writer getWriter(Writer aWriter, Map aMap) throws TemplateModelException, IOException {
		return new GroovyWriter(aWriter, aMap);
	}

	private class GroovyWriter extends Writer {

		private Writer theWriter;
		private Map theParams;
		private StringBuilder theContent;
		private static final String PLUGIN_PARAM = "plugin";

		private GroovyWriter(Writer aWriter, Map someParams) {
			theWriter = aWriter;
			// we must unwrap the objects FreeMarker uses, in order to be able to manipulate them with Groovy
			theParams = new HashMap();
			Iterator it = someParams.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();

				if (entry.getValue() instanceof TemplateModel) {
					try {
						theParams.put(entry.getKey(), DeepUnwrap.unwrap((TemplateModel) entry.getValue()));
					} catch (TemplateModelException e) {
						theParams.put(entry.getKey(), entry.getValue());
					}
				} else {
					theParams.put(entry.getKey(), entry.getValue());
				}
			}
			theContent = new StringBuilder();
		}

		public void write(char cbuf[], int off, int len) throws IOException {
			theContent.append(cbuf, off, len);
		}

		public void flush() throws IOException {
		}

		public void close() throws IOException {
			processScript();
			theWriter.close();
		}

		private void processScript() throws IOException {
			final IGroovyFreeMarkerPlugin freeMarkerPlugin = theLoader.loadPlugin(theParams.get(PLUGIN_PARAM).toString());
			if (freeMarkerPlugin !=null) theWriter.write(freeMarkerPlugin.transform(theParams, theContent.toString()));
			else throw new IOException("Could not find Groovy plugin named "+theParams.get(PLUGIN_PARAM));
		}

	}
}
