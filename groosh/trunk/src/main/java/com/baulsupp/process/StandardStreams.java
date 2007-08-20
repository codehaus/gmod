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

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Yuri Schimke
 *
 */
// TODO don't let stdout, stderr be closed
public class StandardStreams {
	public static class InSource extends Source {
		public void connect(Sink sink) {
			InputStream is = new FileInputStream(FileDescriptor.in);

			if (sink.providesStream()) {
				// TODO feels better if this is line based, rather than fixed
				// buffer size based
				// TODO handle result
				IOUtil.pumpAsync(is, sink.getStream());
			} else if (sink.receivesStream()) {
				sink.setStream(is);
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public static Source stdin() {
		return new InSource();
	}

	public static class ErrSink extends Sink {
		public OutputStream getStream() {
			return new FileOutputStream(FileDescriptor.err) {
				public void close() throws IOException {
					// ignore close
					flush();
				}
			};
		}

		public boolean providesStream() {
			return true;
		}
	}

	public static Sink stderr() {
		return new ErrSink();
	}

	public static class OutSink extends Sink {
		public OutputStream getStream() {
			return new FileOutputStream(FileDescriptor.out) {
				public void close() throws IOException {
					// ignore close
					flush();
				}
			};
		}

		public boolean providesStream() {
			return true;
		}
	}

	public static Sink stdout() {
		return new OutSink();
	}

}
