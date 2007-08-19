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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Yuri Schimke
 *
 */
public class DevNull {
	public static class NullSink extends Sink {
		public OutputStream getStream() {
			return new OutputStream() {
				public void write(int b) throws IOException {
					// do nothing
				}
			};
		}

		public void setStream(final InputStream is) {
			// TODO handle result/exception?
			IOUtil.pumpAsync(is, getStream());
		}

		public boolean providesStream() {
			return true;
		}

		public boolean receivesStream() {
			return true;
		}
	}

	public static class NullSource extends Source {
		public void connect(Sink sink) {
			if (sink.providesStream()) {
				try {
					sink.getStream().close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (sink.receivesStream()) {
				sink.setStream(new ByteArrayInputStream(new byte[0]));
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	public static Sink createSink() {
		return new NullSink();
	}

	public static Source createSource() {
		return new NullSource();
	}
}
