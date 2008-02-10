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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

import org.codehaus.groovy.groosh.process.IOUtil;

/**
 * 
 * @author Yuri Schimke
 * 
 */
public class StringStreams {
	public static class StringSink extends Sink {
		private ByteArrayOutputStream baos = new ByteArrayOutputStream();
		private Future<Integer> result;

		public boolean receivesStream() {
			return true;
		}

		public void setInputStream(InputStream is) {
			result = IOUtil.pumpAsync(is, baos);
		}

		public String toString() {
			try {
				result.get();
			} catch (Exception e) {
				// TODO handle better
				throw new RuntimeException(e);
			}

			try {
				return baos.toString("ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static StringSink stringSink() {
		return new StringSink();
	}

	public static class StringSource extends Source {
		private InputStream is;

		public StringSource(String s) {
			byte[] buffy;

			try {
				buffy = s.getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

			this.is = new ByteArrayInputStream(buffy);
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

	public static StringSource stringSource(String s) {
		return new StringSource(s);
	}
}
