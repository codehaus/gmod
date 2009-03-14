/*
 * Copyright 2009 Mike Dillon
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

package groovyx.posix.io

import org.jruby.ext.posix.POSIX
import org.jruby.ext.posix.POSIXHandler

import com.sun.jna.Native

class DefaultRuntimePOSIXHandler implements POSIXHandler {
    void error(POSIX.ERRORS e, String extraData) {
        switch (e) {
            case POSIX.ERRORS.ENOENT:
                throw new FileNotFoundException(extraData)

            default:
                throw new RuntimeException("Unhandled error: $e: $extraData")
        }
    }

    void unimplementedError(String methodName) {
        throw new UnsupportedOperationException(
            "POSIX function $methodName is not implemented")
    }

    void warn(POSIXHandler.WARNING_ID id, String message, Object[] data) {
        throw new UnsupportedOperationException()
    }

    boolean isVerbose() {
        return false
    }

    File getCurrentWorkingDirectory() {
        return new File(".")
    }

    String[] getEnv() {
        def env = []
        System.getenv().each { k, v ->
            env << "$k=$v"
        }
        return env.toArray(new String[env.size()])
    }

    InputStream getInputStream() {
        return System.in
    }

    PrintStream getOutputStream() {
        return System.out
    }

    int getPID() {
        return Runtime.runtime.hashCode()
    }

    PrintStream getErrorStream() {
        return System.out
    }
}
