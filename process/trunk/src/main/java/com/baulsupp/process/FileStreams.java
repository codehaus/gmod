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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
			if (sink.providesStream()) {
				// TODO handle result
				IOUtil.pumpAsync(is, sink.getStream());
			} else if (sink.receivesStream()) {
				sink.setStream(is);
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public static Source source(File file) throws FileNotFoundException {
		return new FileSource(file);
	}

	public static class FileSink extends Sink {
		private FileOutputStream os;

		public FileSink(File f, boolean append) throws FileNotFoundException {
			this.os = new FileOutputStream(f, append);
		}

		public OutputStream getStream() {
			return os;
		}

		public boolean providesStream() {
			return true;
		}
	}

	public static Sink sink(File file, boolean append)
			throws FileNotFoundException {
		return new FileSink(file, append);
	}
}
