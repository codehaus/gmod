import org.codehaus.groovy.gfreemarker.freemarker.FreeMarkerTemplateEngine
import org.junit.Test
import static org.junit.Assert.*;

/**
 * A very basic test for Groovy Freemarker
 * User: cedric
 * Date: 2 août 2007
 * Time: 20:09:19
 */
public class FreeMarkerTest {

	@Test public void templateIsTransformed() {
		def tpl = '''Hello, ${user.name} : <@groovy plugin="urlencoder" mode=user>this is a test ${user.name}</@groovy>'''
		def loader = this.class.classLoader
		def path = loader.getResource("plugins").getPath()
		def engine = new FreeMarkerTemplateEngine(path)
		def binding = ["user": ["name": "cedric"]]
		def out = engine.createTemplate(tpl).make(binding).toString()
		assertEquals('''Hello, cedric : this+is+a+test+cedric''', out)
	}
}