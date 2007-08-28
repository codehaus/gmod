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

import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

import groovy.lang.Closure;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class GridClosureProcess extends StreamClosureProcess {
	public GridClosureProcess(Closure closure) {
		super(closure);
	}

	protected void process(final InputStream is, final OutputStream os)
			throws IOException {
		BufferedReader ris = new BufferedReader(new InputStreamReader(is));
		Writer wos = new PrintWriter(new OutputStreamWriter(os, "ISO-8859-1"));

		String line;

		List<Object> l = new ArrayList<Object>();

		while ((line = ris.readLine()) != null) {
			String[] content = line.split("\\s+");
			List<String> contentList = Arrays.asList(content);

			l.clear();
			l.add(contentList);
			l.add(wos);
			closure.call(l);
			wos.flush();
		}
	}
}
