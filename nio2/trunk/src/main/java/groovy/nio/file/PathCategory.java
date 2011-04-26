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

import groovy.lang.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.InvokerHelper;

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
        return (Long)Files.readAttributes(self, "size").get("size");
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
        return DefaultGroovyMethods.eachLine(Files.newInputStream(self), 1, closure);
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
    	return DefaultGroovyMethods.readLines(Files.newInputStream(self));
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
        return DefaultGroovyMethods.getText(Files.newInputStream(self));
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
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(self)));
            writer.write(text);
            writer.flush();

            Writer temp = writer;
            writer = null;
            temp.close();
        } finally {
        	DefaultGroovyMethodsSupport.closeWithWarning(writer);
        }
    }
    
    /**
     * Append the text at the end of the Path.
     *
     * @param self a Path
     * @param text the text to append at the end of the Path
     * @throws IOException if an IOException occurs.
     * @since ??
     */
    public static void append(Path self, Object text) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(self, StandardOpenOption.APPEND)));
            InvokerHelper.write(writer, text);
            writer.flush();

            Writer temp = writer;
            writer = null;
            temp.close();
        } finally {
        	DefaultGroovyMethodsSupport.closeWithWarning(writer);
        }
    }

    public static void deleteOnExit(Path self) {
        self.toFile().deleteOnExit();
    }

    public static InputStream newInputStream(Path self) throws IOException {
        return Files.newInputStream(self);
    }

    public static OutputStream newOutputStream(Path self) throws IOException {
        return Files.newOutputStream(self);
    }

    public static <T> T withReader(Path self, Closure<T> closure) throws IOException {
        return DefaultGroovyMethods.withReader(newReader(self), closure);
    }

    public static BufferedReader newReader(Path self) throws IOException {
        return DefaultGroovyMethods.newReader(self.toFile()); // side-step CharsetToolkit particulars
    }

    // TODO replace these generated methods with more efficient implementations:

    //public static long size(Path self) { return DefaultGroovyMethods.size(self.toFile()); }
    public static ObjectOutputStream newObjectOutputStream(Path path) throws IOException { return DefaultGroovyMethods.newObjectOutputStream(path.toFile()); }
    public static <T> T withObjectOutputStream(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withObjectOutputStream(path.toFile(),closure); }
    public static ObjectInputStream newObjectInputStream(Path path) throws IOException { return DefaultGroovyMethods.newObjectInputStream(path.toFile()); }
    public static ObjectInputStream newObjectInputStream(Path path, final ClassLoader classLoader) throws IOException { return DefaultGroovyMethods.newObjectInputStream(path.toFile(),classLoader); }
    public static void eachObject(Path self, Closure closure) throws IOException, ClassNotFoundException { DefaultGroovyMethods.eachObject(self.toFile(),closure); }
    public static <T> T withObjectInputStream(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withObjectInputStream(path.toFile(),closure); }
    public static <T> T withObjectInputStream(Path path, ClassLoader classLoader, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withObjectInputStream(path.toFile(),classLoader,closure); }
    //public static <T> T eachLine(Path self, Closure<T> closure) throws IOException { return DefaultGroovyMethods.eachLine(self.toFile(),closure); }
    public static <T> T eachLine(Path self, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.eachLine(self.toFile(),charset,closure); }
    public static <T> T eachLine(Path self, int firstLine, Closure<T> closure) throws IOException { return DefaultGroovyMethods.eachLine(self.toFile(),firstLine,closure); }
    public static <T> T eachLine(Path self, String charset, int firstLine, Closure<T> closure) throws IOException { return DefaultGroovyMethods.eachLine(self.toFile(),charset,firstLine,closure); }
    public static <T> T splitEachLine(Path self, String regex, Closure<T> closure) throws IOException { return DefaultGroovyMethods.splitEachLine(self.toFile(),regex,closure); }
    public static <T> T splitEachLine(Path self, Pattern pattern, Closure<T> closure) throws IOException { return DefaultGroovyMethods.splitEachLine(self.toFile(),pattern,closure); }
    public static <T> T splitEachLine(Path self, String regex, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.splitEachLine(self.toFile(),regex,charset,closure); }
    public static <T> T splitEachLine(Path self, Pattern pattern, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.splitEachLine(self.toFile(),pattern,charset,closure); }
    //public static List<String> readLines(Path path) throws IOException { return DefaultGroovyMethods.readLines(path.toFile()); }
    public static List<String> readLines(Path path, String charset) throws IOException { return DefaultGroovyMethods.readLines(path.toFile(),charset); }
    public static String getText(Path path, String charset) throws IOException { return DefaultGroovyMethods.getText(path.toFile(),charset); }
    //public static String getText(Path path) throws IOException { return DefaultGroovyMethods.getText(path.toFile()); }
    public static byte[] getBytes(Path path) throws IOException { return DefaultGroovyMethods.getBytes(path.toFile()); }
    public static void setBytes(Path path, byte[] bytes) throws IOException { DefaultGroovyMethods.setBytes(path.toFile(),bytes); }
    //public static void write(Path path, String text) throws IOException { DefaultGroovyMethods.write(path.toFile(),text); }
    public static void setText(Path path, String text) throws IOException { DefaultGroovyMethods.setText(path.toFile(),text); }
    public static void setText(Path path, String text, String charset) throws IOException { DefaultGroovyMethods.setText(path.toFile(),text,charset); }
    public static Path leftShift(Path path, Object text) throws IOException { return DefaultGroovyMethods.leftShift(path.toFile(),text).toPath(); }
    public static Path leftShift(Path path, byte[] bytes) throws IOException { return DefaultGroovyMethods.leftShift(path.toFile(),bytes).toPath(); }
    public static Path leftShift(Path path, InputStream data) throws IOException { return DefaultGroovyMethods.leftShift(path.toFile(),data).toPath(); }
    public static void write(Path path, String text, String charset) throws IOException { DefaultGroovyMethods.write(path.toFile(),text,charset); }
    //public static void append(Path path, Object text) throws IOException { DefaultGroovyMethods.append(path.toFile(),text); }
    public static void append(Path path, byte[] bytes) throws IOException { DefaultGroovyMethods.append(path.toFile(),bytes); }
    public static void append(Path self, InputStream stream ) throws IOException { DefaultGroovyMethods.append(self.toFile(),stream); }
    public static void append(Path path, Object text, String charset) throws IOException { DefaultGroovyMethods.append(path.toFile(),text,charset); }
    public static void eachDir(Path self, Closure closure) throws FileNotFoundException, IllegalArgumentException { DefaultGroovyMethods.eachDir(self.toFile(),closure); }
    public static void eachFileRecurse(Path self, Closure closure) throws FileNotFoundException, IllegalArgumentException { DefaultGroovyMethods.eachFileRecurse(self.toFile(),closure); }
    //public static BufferedReader newReader(Path path) throws IOException { return DefaultGroovyMethods.newReader(path.toFile()); }
    public static BufferedReader newReader(Path path, String charset) throws IOException { return DefaultGroovyMethods.newReader(path.toFile(),charset); }
    //public static <T> T withReader(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withReader(path.toFile(),closure); }
    public static <T> T withReader(Path path, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withReader(path.toFile(),charset,closure); }
    //public static BufferedOutputStream newOutputStream(Path path) throws IOException { return DefaultGroovyMethods.newOutputStream(path.toFile()); }
    public static DataOutputStream newDataOutputStream(Path path) throws IOException { return DefaultGroovyMethods.newDataOutputStream(path.toFile()); }
    public static Object withOutputStream(Path path, Closure closure) throws IOException { return DefaultGroovyMethods.withOutputStream(path.toFile(),closure); }
    public static Object withInputStream(Path path, Closure closure) throws IOException { return DefaultGroovyMethods.withInputStream(path.toFile(),closure); }
    public static <T> T withDataOutputStream(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withDataOutputStream(path.toFile(),closure); }
    public static <T> T withDataInputStream(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withDataInputStream(path.toFile(),closure); }
    public static BufferedWriter newWriter(Path path) throws IOException { return DefaultGroovyMethods.newWriter(path.toFile()); }
    public static BufferedWriter newWriter(Path path, boolean append) throws IOException { return DefaultGroovyMethods.newWriter(path.toFile(),append); }
    public static BufferedWriter newWriter(Path path, String charset, boolean append) throws IOException { return DefaultGroovyMethods.newWriter(path.toFile(),charset,append); }
    public static BufferedWriter newWriter(Path path, String charset) throws IOException { return DefaultGroovyMethods.newWriter(path.toFile(),charset); }
    public static <T> T withWriter(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withWriter(path.toFile(),closure); }
    public static <T> T withWriter(Path path, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withWriter(path.toFile(),charset,closure); }
    public static <T> T withWriterAppend(Path path, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withWriterAppend(path.toFile(),charset,closure); }
    public static <T> T withWriterAppend(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withWriterAppend(path.toFile(),closure); }
    public static PrintWriter newPrintWriter(Path path) throws IOException { return DefaultGroovyMethods.newPrintWriter(path.toFile()); }
    public static PrintWriter newPrintWriter(Path path, String charset) throws IOException { return DefaultGroovyMethods.newPrintWriter(path.toFile(),charset); }
    public static <T> T withPrintWriter(Path path, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withPrintWriter(path.toFile(),closure); }
    public static <T> T withPrintWriter(Path path, String charset, Closure<T> closure) throws IOException { return DefaultGroovyMethods.withPrintWriter(path.toFile(),charset,closure); }
    //public static BufferedInputStream newInputStream(Path path) throws FileNotFoundException { return DefaultGroovyMethods.newInputStream(path.toFile()); }
    public static DataInputStream newDataInputStream(Path path) throws FileNotFoundException { return DefaultGroovyMethods.newDataInputStream(path.toFile()); }
    public static void eachByte(Path self, Closure closure) throws IOException { DefaultGroovyMethods.eachByte(self.toFile(),closure); }
    public static void eachByte(Path self, int bufferLen, Closure closure) throws IOException { DefaultGroovyMethods.eachByte(self.toFile(),bufferLen,closure); }
    public static Writable filterLine(Path self, Closure closure) throws IOException { return DefaultGroovyMethods.filterLine(self.toFile(),closure); }
    public static Writable filterLine(Path self, String charset, Closure closure) throws IOException { return DefaultGroovyMethods.filterLine(self.toFile(),charset,closure); }
    public static void filterLine(Path self, Writer writer, Closure closure) throws IOException { DefaultGroovyMethods.filterLine(self.toFile(),writer,closure); }
    public static void filterLine(Path self, Writer writer, String charset, Closure closure) throws IOException { DefaultGroovyMethods.filterLine(self.toFile(),writer,charset,closure); }
    //public static byte[] readBytes(Path path) throws IOException { return DefaultGroovyMethods.readBytes(path.toFile()); }
    public static Path asWritable(Path path) { return DefaultGroovyMethods.asWritable(path.toFile()).toPath(); }
    public static <T> T asType(Path f, Class<T> c) { return DefaultGroovyMethods.asType(f.toFile(),c); }
    public static Path asWritable(Path path, String encoding) { return DefaultGroovyMethods.asWritable(path.toFile(),encoding).toPath(); }


}
