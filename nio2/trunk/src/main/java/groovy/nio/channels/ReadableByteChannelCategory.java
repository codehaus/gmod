/*
 * Copyright 2003-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package groovy.nio.channels;

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * This class defines new Java 7 specific groovy methods which extend the normal
 * JDK classes inside the Groovy environment. Static methods are used with the
 * first parameter the destination class.
 *
 * @author Merlyn Albery-Speyer
 */
public class ReadableByteChannelCategory {
    /**
     * Read the content of the Path and return it as a String. The readable byte channel
     * is closed after reading. (Only works with JDK1.7 or later).
     *
     * @param readableByteChannel a ReadableByteChannel whose content we want to read
     * @return a String containing the content of the readable byte channel
     * @throws IOException if an IOException occurs.
     * @since ??
     */
    public static String getText(ReadableByteChannel readableByteChannel) throws IOException {
        StringBuilder answer = new StringBuilder();
        byte[] buf = new byte[8192];
        ByteBuffer byteBuf = ByteBuffer.wrap(buf);        
        
        try {        
            int read;
            while ((read = readableByteChannel.read(byteBuf)) > 0) {
                byteBuf.rewind();
                
                answer.append(new String(buf, 0, read));

                byteBuf.flip(); 
            }
            ReadableByteChannel temp = readableByteChannel;
            readableByteChannel = null;            
            temp.close();
        } finally {
            DefaultGroovyMethodsSupport.closeWithWarning(readableByteChannel);
        }

        return answer.toString();
    }
}
