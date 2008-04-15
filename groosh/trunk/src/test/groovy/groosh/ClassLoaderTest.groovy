package groosh

class ClassLoaderTest extends GroovyTestCase {
	void testClassLoader() {
		String url = this.class.classLoader.getResource("org").toString()
		assert url.endsWith("org");
	}
}