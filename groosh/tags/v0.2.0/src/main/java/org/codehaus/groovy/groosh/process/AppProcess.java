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

package org.codehaus.groovy.groosh.process;

import java.io.IOException;

/**
 * 
 * @author Yuri Schimke
 * 
 */
// TODO how does the completion of the input/output i.e. to a file get
// monitored?
public interface AppProcess {
	Sink getInput();

	Source getOutput();

	Source getError();

	void start() throws IOException;

	int result();

	boolean hadError();

	void destroy();
}
