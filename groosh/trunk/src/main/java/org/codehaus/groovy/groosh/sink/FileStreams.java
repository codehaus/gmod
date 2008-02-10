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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.codehaus.groovy.groosh.process.IOUtil;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class FileStreams {
	public static class FileSource extends Source {
		private FileInputStream is;

		public FileSource(File f) throws FileNotFoundException {
			this.is = new FileInputStream(f);
		}

		public void connect(Sink sink) {
			if (sink.providesOutputStream()) {
				streamPumpResult = IOUtil.pumpAsync(is, sink.getOutputStream());
			} else if (sink.receivesStream()) {
				sink.setInputStream(is);
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public static Source fileSource(File file) throws FileNotFoundException {
		return new FileSource(file);
	}

	public static class FileSink extends Sink {
		private FileOutputStream os;

		public FileSink(File f, boolean append) throws FileNotFoundException {
			this.os = new FileOutputStream(f, append);
		}

		@Override
		public OutputStream getOutputStream() {
			return os;
		}

		@Override
		public boolean providesOutputStream() {
			return true;
		}
	}

	public static Sink fileSink(File file, boolean append)
			throws FileNotFoundException {
		return new FileSink(file, append);
	}
}
