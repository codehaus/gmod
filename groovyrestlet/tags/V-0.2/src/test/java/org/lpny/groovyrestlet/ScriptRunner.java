/**
 * 
 */
package org.lpny.groovyrestlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Router;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author keke
 * 
 */
public class ScriptRunner {
	private GroovyRestlet fixture;
	private static final String ROOT = "./src/test/groovy/org/lpny/groovyrestlet/scripts";

	@Test(groups = { "unittest" })
	public void testBuildRouter() throws IOException {
		Context ctx = new Context();
		Map<String, Object> binding = new HashMap<String, Object>();
		binding.put("myCtx", ctx);
		Router router = (Router) fixture.build(binding, new FileInputStream(
				new File(ROOT, "BuilderRouter.groovy")));
		assert router != null;
		assert router.getContext() == ctx;
	}

	@BeforeClass(groups = { "unittest" })
	public void setup() {
		fixture = new GroovyRestlet(new ClassPathXmlApplicationContext(
				"./testSpringContext.xml"));
	}

	private void run(final String name) {
		fixture.build(new File(ROOT, name).toURI());
	}
}
