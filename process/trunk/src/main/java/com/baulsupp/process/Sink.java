//  !!projectname!! -- Provides a shell-like capability for handling external processes
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

package com.baulsupp.process;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Yuri Schimke
 *
 */
// TODO need an isFinished method
// in general process.waitForExit() knows when input has finished
// but not aware of output has reached destination neccesarily! 
public class Sink {
	public boolean receivesStream() {
		return false;
	}

	public boolean providesStream() {
		return false;
	}

	public OutputStream getStream() {
		throw new UnsupportedOperationException();
	}

	public void setStream(InputStream channel) {
		throw new UnsupportedOperationException();
	}
}
