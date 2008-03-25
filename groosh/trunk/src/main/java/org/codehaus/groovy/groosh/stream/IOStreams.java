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

package org.codehaus.groovy.groosh.stream;

import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.groovy.util.IOUtil;

/**
 * 
 * @author Alexander Egger
 * 
 */
public class IOStreams {
	public static class IOSource extends Source {
		private InputStream is;

		public IOSource(InputStream is) {
			this.is = is;
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

	public static Source inputStreamSource(InputStream is) {
		return new IOSource(is);
	}

	public static class IOSink extends Sink {
		private OutputStream os;

		public IOSink(OutputStream os) {
			this.os = os;
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

	public static Sink outputStreamSink(OutputStream os) {
		return new IOSink(os);
	}

	public static class InputStreamSink extends Sink {

		private InputStream is;

		@Override
		public boolean receivesStream() {
			return true;
		}

		@Override
		public boolean providesOutputStream() {
			return false;
		}

		@Override
		public OutputStream getOutputStream() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setInputStream(InputStream channel) {
			is = channel;
		}

		public InputStream getInputStream() {
			return is;
		}
	}

	public static InputStreamSink inputStreamSink() {
		return new InputStreamSink();
	}
}
