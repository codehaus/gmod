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

package org.codehaus.groovy.groosh.sink;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class Sink {

	public boolean receivesStream() {
		return false;
	}

	public boolean providesOutputStream() {
		return false;
	}

	public OutputStream getOutputStream() {
		throw new UnsupportedOperationException();
	}

	public void setInputStream(InputStream channel) {
		throw new UnsupportedOperationException();
	}

}
