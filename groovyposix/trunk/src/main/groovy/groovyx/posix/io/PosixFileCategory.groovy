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

import org.jruby.ext.posix.FileStat
import org.jruby.ext.posix.POSIX
import org.jruby.ext.posix.POSIXFactory

import com.sun.jna.Native

class PosixFileCategory {
    private static handler = new DefaultRuntimePOSIXHandler()
    private static posix = POSIXFactory.getPOSIX(handler, true)

    static void chmod(File file, int mode) {
        if (posix.chmod(file.path, mode)) {
            handleError('chmod', file.path)
        }
    }

    static void lchmod(File file, int mode) {
        if (posix.lchmod(file.path, mode)) {
            handleError('lchmod', file.path)
        }
    }

    static void chown(File file, int uid, int gid) {
        if (posix.chown(file.path, uid, gid)) {
            handleError('chown', file.path)
        }
    }

    static void lchown(File file, int uid, int gid) {
        if (posix.lchown(file.path, uid, gid)) {
            handleError('lchown', file.path)
        }
    }

    static FileStat stat(File file) {
        return posix.stat(file.path)
    }

    static FileStat lstat(File file) {
        return posix.lstat(file.path)
    }

    static File link(File file, File targetFile) {
        String targetPath = targetFile == null ? null : targetFile.path
        link(file, targetPath)
    }

    static File link(File file, String targetPath) {
        if (posix.link(file.path, targetPath)) {
            handleError('link', file.path)
        }
        return new File(targetPath)
    }

    static File symlink(File file, File targetFile) {
        String targetPath = targetFile == null ? null : targetFile.path
        symlink(file, targetPath)
    }

    static File symlink(File file, String targetPath) {
        if (posix.symlink(file.path, targetPath)) {
            handleError('symlink', file.path)
        }
        return new File(targetPath)
    }

    static File readlink(File file) throws IOException {
        def targetPath = posix.readlink(file.path)
        return targetPath == null ? null : new File(file.parent, targetPath)
    }

    static boolean isRegularFile(File file) {
        return stat(file).isFile()
    }

    static boolean isSymlink(File file) {
        return stat(file).isSymlink()
    }

    static boolean isFifo(File file) {
        return stat(file).isFifo()
    }

    static boolean isNamedPipe(File file) {
        return stat(file).isNamedPipe()
    }

    static boolean isSocket(File file) {
        return stat(file).isSocket()
    }

    static boolean isBlockDevice(File file) {
        return stat(file).isBlockDev()
    }

    static boolean isCharacterDevice(File file) {
        return stat(file).isCharDev()
    }

    static long getInodeNumber(File file) {
        return getInodeNumber(file, true)
    }

    static long getInodeNumber(File file, boolean followLinks) {
        def stat = followLinks ? stat(file) : lstat(file)
        return stat.ino()
    }

    static PosixFileType getFileType(File file) {
        return getFileType(file, true)
    }

    static PosixFileType getFileType(File file, boolean followLinks) {
        def stat = followLinks ? stat(file) : lstat(file)
        return PosixFileType.valueOf(stat.ftype())
    }

    static int getOwnerId(File file) {
        return getOwner(file, true)
    }

    static int getOwnerId(File file, boolean followLinks) {
        def stat = followLinks ? stat(file) : lstat(file)
        return stat.uid()
    }

    static int getGroupOwnerId(File file) {
        return getGroupOwner(file, true)
    }

    static int getGroupOwnerId(File file, boolean followLinks) {
        def stat = followLinks ? stat(file) : lstat(file)
        return stat.gid()
    }

    static long getDeviceId(File file) {
        return getDeviceId(file, true)
    }

    static long getDeviceId(File file, boolean followLinks) {
        def stat = followLinks ? stat(file) : lstat(file)
        return stat.dev()
    }

    static long getRDeviceId(File file) {
        return getRDeviceId(file, true)
    }

    static long getRDeviceId(File file, boolean followLinks) {
        def stat = followLinks ? stat(file) : lstat(file)
        return stat.rdev()
    }

    private static void handleError(methodName, path) {
        // def errno = posix.errno()
        // posix.errno(0)
        def errno = Native.lastError
        Native.lastError = 0

        switch (errno) {
            // POSIX.ERRORS.ENOENT
            case 2:
                handler.error(POSIX.ERRORS.ENOENT, path)

            default:
                throw new RuntimeException(
                    "Error calling POSIX function $methodName: $errno")
        }
    }
}
