package groovyx.posix.io

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat
import org.junit.Test

class PosixFileCategoryTest {
    @Test
    void testStat() {
        use (PosixFileCategory) {
            def cwd = new File(".")

            def stat = cwd.stat()
            assertThat stat, is(not(nullValue()))

            assertThat stat.ftype(), is("directory")
            assertThat stat.isDirectory(), is(true)
        }
    }
}
