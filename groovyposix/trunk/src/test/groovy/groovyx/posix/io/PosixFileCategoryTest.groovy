package groovyx.posix.io

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class PosixFileCategoryTest {
    private static File rootDir
    private static File dataDir

    @BeforeClass
    static void setUp() {
        rootDir = File.listRoots()[0]

        // Assumes that working directory is already "target"
        dataDir = new File("test-data-dir")
        dataDir.mkdirs()
    }

    @Test
    void testStatLstat() {
        use (PosixFileCategory) {
            def stat = rootDir.stat()
            def lstat = rootDir.lstat()

            [ stat, lstat ].each {
                assertThat it, is(not(nullValue()))

                assertThat it.ftype(), is("directory")
                assertThat it.directory, is(true)
                assertThat it.isSymlink(), is(false)
            }

            // FileStat doesn't implement equals() properly, so compare device
            // numbers and inode to assert that stat and lstat are identical
            assertThat stat.dev(), is(equalTo(lstat.dev()))
            assertThat stat.rdev(), is(equalTo(lstat.rdev()))
            assertThat stat.ino(), is(equalTo(lstat.ino()))
        }
    }

    @Test
    void testSymlinkReadlink() {
        use (PosixFileCategory) {
            assertThat rootDir.readlink(), is(nullValue())
            assertThat rootDir.isSymlink(), is(false)

            def linkToRoot = new File(dataDir, "link-to-root")
            linkToRoot.delete()
            assertThat linkToRoot.exists(), is(false)

            linkToRoot.symlink(rootDir)
            assertThat linkToRoot.isSymlink(), is(true)
            assertThat linkToRoot.readlink(), is(rootDir)
            assertThat linkToRoot.exists(), is(true)

            linkToRoot.delete()

            // Create local target file
            def localFile = new File(dataDir, "local-target")
            localFile.text = ""
            assertThat localFile.exists(), is(true)

            // Link to local target file
            def linkToLocal = new File(dataDir, "link-to-local")
            linkToLocal.delete()
            assertThat linkToLocal.exists(), is(false)

            linkToLocal.symlink(localFile.name)
            assertThat linkToLocal.isSymlink(), is(true)
            assertThat linkToLocal.readlink(), is(localFile)
            assertThat linkToLocal.exists(), is(true)

            linkToLocal.delete()
            localFile.delete()
        }
    }

    @Test
    void testStatHelpers() {
        use (PosixFileCategory) {
            // Create local target file
            def localFile = new File(dataDir, "local-target")
            localFile.text = ""
            assertThat localFile.exists(), is(true)

            // Test the helpers for the local file
            assertThat localFile.isRegularFile(), is(true)
            assertThat localFile.isSymlink(), is(false)
            assertThat localFile.isNamedPipe(), is(false)
            assertThat localFile.isFifo(), is(false)
            assertThat localFile.isSocket(), is(false)
            assertThat localFile.isBlockDevice(), is(false)
            assertThat localFile.isCharacterDevice(), is(false)
            assertThat localFile.getFileType(), is(PosixFileType.file)

            // Clean up
            localFile.delete()
        }
    }
}
