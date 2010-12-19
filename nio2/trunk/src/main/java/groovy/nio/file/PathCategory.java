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

package groovy.nio.file;

import groovy.lang.Closure;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.Attributes;
import java.util.List;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;

/**
 * This class defines new Java 7 specific groovy methods which extend the normal
 * JDK classes inside the Groovy environment. Static methods are used with the
 * first parameter the destination class.
 *
 * @author Merlyn Albery-Speyer
 */
public class PathCategory {
    /**
     * Provide the standard Groovy <code>size()</code> method for <code>Path</code>.
     *
     * @param self a file object
     * @return the file's size (length)
     * @throws IOException 
     * @since ??
     */
    public static long size(Path self) throws IOException {
        return Attributes.readBasicFileAttributes(self).size();
    }
    
    /**
     * Iterates through this file line by line.  Each line is passed to the
     * given 1 or 2 arg closure.  The file is read using a reader which
     * is closed before this method returns.
     *
     * @param self    a Path
     * @param closure a closure (arg 1 is line, optional arg 2 is line number starting at line 1)
     * @throws IOException if an IOException occurs.
     * @return the last value returned by the closure
     * @see #eachLine(java.io.File, int, groovy.lang.Closure)
     * @since ??
     */
    public static <T> T eachLine(Path self, Closure<T> closure) throws IOException {
        return DefaultGroovyMethods.eachLine(self.newInputStream(), 1, closure);
    }
    
    /**
     * Reads the file into a list of Strings, with one item for each line.
     *
     * @param self a Path
     * @return a List of lines
     * @throws IOException if an IOException occurs.
     * @see #readLines(java.io.Reader)
     * @since ??
     */
    public static List<String> readLines(Path self) throws IOException {
    	return DefaultGroovyMethods.readLines(self.newInputStream());
    }
    
    /**
     * Read the content of the Path and returns it as a String.
     *
     * @param self the file whose content we want to read
     * @return a String containing the content of the file
     * @throws IOException if an IOException occurs.
     * @since 1.0
     */
    public static String getText(Path self) throws IOException {
        return DefaultGroovyMethods.getText(self.newInputStream());
    }
    
    /**
     * Write the text to the Path.
     *
     * @param self a Path
     * @param text the text to write to the File
     * @throws IOException if an IOException occurs.
     * @since ??
     */
    public static void write(Path self, String text) throws IOException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(self.newOutputStream());
            out.write(text.getBytes());
            out.flush();

            BufferedOutputStream temp = out;
            out = null;
            temp.close();
        } finally {
        	DefaultGroovyMethodsSupport.closeWithWarning(out);
        }
    }
}
