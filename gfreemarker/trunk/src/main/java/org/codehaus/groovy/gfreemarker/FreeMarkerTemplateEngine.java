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

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import groovy.lang.Writable;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * A template engine that leverages on the FreeMarker template engine.
 * http://www.freemarker.org
 * User: cedric
 * Date: 2 août 2007
 * Time: 18:12:42
 */
public class FreeMarkerTemplateEngine extends TemplateEngine {

	private static final String NOT_CACHED_TEMPLATE = "default-groovy-template";

	private IGroovyFreeMarkerPluginLoader theLoader;
	private Configuration theConfiguration;
	private Map theCache;
	// todo : we use our own cache, as templates to be cached are groovy templates, not directly FreeMarker ones.
	// however, the FreeMarker caching system is more elaborate than this one, so anyone wishing to inspire
	// from it could do so ;)


	private FreeMarkerTemplateEngine() {
		theConfiguration = new Configuration();
		theConfiguration.setObjectWrapper(new BeansWrapper());
		theCache = new HashMap();
	}

	public FreeMarkerTemplateEngine(IGroovyFreeMarkerPluginLoader aPluginLoader) {
		this();
		theLoader = aPluginLoader;
	}

	public FreeMarkerTemplateEngine(String aGroovyScriptsPath) {
		this();
		theLoader = new SimpleGroovyFreeMarkerPluginLoader(aGroovyScriptsPath);
	}

	/**
	 * Allows you to adjust the FreeMarker configuration. USE WITH CARE.
	 * @return &lt;code>Configuration&lt;/code> the FreeMarker configuration
	 */
	public Configuration getConfiguration() {
		return theConfiguration;
	}

	/**
	 * Creates a template from a Reader. The template will not be cached.
	 * @param aReader
	 * @return
	 * @throws CompilationFailedException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Template createTemplate(Reader aReader) throws CompilationFailedException, ClassNotFoundException, IOException {
		GroovyFreeMarkerTemplate template = new GroovyFreeMarkerTemplate(NOT_CACHED_TEMPLATE);
		template.parse(aReader);
		return template;
	}

	/**
	 * Creates a named template from a Reader. The template will be sent to the cache.
	 * @param aTemplateName
	 * @param aReader
	 * @return
	 * @throws CompilationFailedException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Template createNamedTemplate(String aTemplateName, Reader aReader) throws CompilationFailedException, ClassNotFoundException, IOException {
		GroovyFreeMarkerTemplate template = new GroovyFreeMarkerTemplate(aTemplateName);
		template.parse(aReader);
		if (!aTemplateName.equals(NOT_CACHED_TEMPLATE)) {
			theCache.put(aTemplateName, template);
		}
		return template;
	}


	/**
	 * Retrieves a template from the cache.
	 * @param aTemplateName the template identifier.
	 * @return
	 */
	public Template getNamedTemplate(String aTemplateName) {
		return (Template) theCache.get(aTemplateName);
	}

	/**
	 * Removes a template from the cache.
 	 * @param aTemplateName the template identifier
	 */
	public void removeNamedTemplate(String aTemplateName) {
		theCache.remove(aTemplateName);
	}

	/**
	 * Clears the cache
 	 */
	public void purgeCache() {
		theCache.clear();
	}

	private class GroovyFreeMarkerTemplate implements Template {

		private String theID;
		private Reader theReader;

		private GroovyFreeMarkerTemplate(String aID) {
			theID = aID;
		}

		public Writable make() {
			return make(null);
		}

		public Writable make(final Map aMap) {
			return new Writable() {
				public Writer writeTo(Writer aWriter) throws IOException {
					freemarker.template.Template tpl = new freemarker.template.Template(theID, theReader, theConfiguration);
					try {
						aMap.put("groovy", new GroovyTransformModel(theLoader));
						tpl.process(aMap, aWriter);
					} catch (TemplateException e) {
						e.printStackTrace();
					}
					return aWriter;
				}

				public String toString() {
					try {
                        StringWriter sw = new StringWriter();
                        writeTo(sw);
                        return sw.toString();
                    } catch (Exception e) {
                        return e.toString();
                    }
				}
			};
		}

		/**
		 * Use a parse method in order to lately manage dynamic caching
		 * @param aReader
		 */
		private void parse(Reader aReader) {
			theReader = aReader;
		}
	}
}
