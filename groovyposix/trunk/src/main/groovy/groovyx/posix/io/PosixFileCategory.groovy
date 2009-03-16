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

    static void chown(File file, int uid) {
        if (posix.chown(file.path, uid, -1)) {
            handleError('chown', file.path)
        }
    }

    static void chgrp(File file, int gid) {
        if (posix.chgrp(file.path, -1, gid)) {
            handleError('chgrp', file.path)
        }
    }

    static void lchown(File file, int uid, int gid) {
        if (posix.lchown(file.path, uid, gid)) {
            handleError('lchown', file.path)
        }
    }

    static void lchown(File file, int uid) {
        if (posix.lchown(file.path, uid, -1)) {
            handleError('lchown', file.path)
        }
    }

    static void lchgrp(File file, int gid) {
        if (posix.lchgrp(file.path, -1, gid)) {
            handleError('lchgrp', file.path)
        }
    }

    static FileStat stat(File file) {
        return posix.stat(file.path)
    }

    static FileStat lstat(File file) {
        return posix.lstat(file.path)
    }

    static FileStat stat(File file, boolean followLinks) {
        return followLinks ? stat(file) : lstat(file)
    }

    static File link(File file, File targetFile) {
        String targetPath = targetFile == null ? null : targetFile.path
        return link(file, targetPath)
    }

    static File link(File file, String targetPath) {
        if (posix.link(targetPath, file.path)) {
            handleError('link', file.path)
        }
        return pathToFile(file.parentFile, targetPath)
    }

    static File symlink(File file, File targetFile) {
        String targetPath = targetFile == null ? null : targetFile.absolutePath
        return symlink(file, targetPath)
    }

    static File symlink(File file, String targetPath) {
        if (posix.symlink(targetPath, file.path)) {
            handleError('symlink', file.path)
        }
        return pathToFile(file.parentFile, targetPath)
    }

    static File readlink(File file) throws IOException {
        def targetPath = posix.readlink(file.path)
        return pathToFile(file.parentFile, targetPath)
    }

    static boolean isRegularFile(File file) {
        return stat(file).isFile()
    }

    static boolean isSymlink(File file) {
        // Note: lstat is used here instead of stat, else you'd never see true
        // for a symlink (except dangling ones)
        return lstat(file).isSymlink()
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
        return stat(file, followLinks).ino()
    }

    static PosixFileType getFileType(File file) {
        return getFileType(file, true)
    }

    static PosixFileType getFileType(File file, boolean followLinks) {
        return PosixFileType.valueOf(stat(file, followLinks).ftype())
    }

    static int getOwnerId(File file) {
        return getOwner(file, true)
    }

    static int getOwnerId(File file, boolean followLinks) {
        return stat(file, followLinks).uid()
    }

    static int getGroupOwnerId(File file) {
        return getGroupOwner(file, true)
    }

    static int getGroupOwnerId(File file, boolean followLinks) {
        return stat(file, followLinks).gid()
    }

    static long getDeviceId(File file) {
        return getDeviceId(file, true)
    }

    static long getDeviceId(File file, boolean followLinks) {
        return stat(file, followLinks).dev()
    }

    static long getRDeviceId(File file) {
        return getRDeviceId(file, true)
    }

    static long getRDeviceId(File file, boolean followLinks) {
        return stat(file, followLinks).rdev()
    }

    private static File pathToFile(File referenceDirectory, String path) {
        def file
        if (path != null) {
            file = new File(path)
            if (!file.absolute) {
                file = new File(referenceDirectory, path)
            }
        }
        return file
    }

    private static void handleError(methodName, path) {
        // def errno = posix.errno()
        // posix.errno(0)
        def errno = Native.lastError
        Native.lastError = 0

        switch (errno) {
            // POSIX.ERRORS.EPERM
            case 1:
                handler.error(POSIX.ERRORS.EPERM, path)

            // POSIX.ERRORS.ENOENT
            case 2:
                handler.error(POSIX.ERRORS.ENOENT, path)

            // POSIX.ERRORS.EACCES
            case 13:
                handler.error(POSIX.ERRORS.EEXIST, path)

            // POSIX.ERRORS.EEXIST
            case 17:
                handler.error(POSIX.ERRORS.EEXIST, path)

            default:
                throw new RuntimeException(
                    "Error calling POSIX function $methodName: $errno")
        }
    }
}
