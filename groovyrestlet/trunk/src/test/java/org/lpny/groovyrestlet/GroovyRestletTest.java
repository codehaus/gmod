/**
 * 
 */
package org.lpny.groovyrestlet;

import java.io.File;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

/**
 * 
 * @author keke
 * 
 */
@Test(groups = { "unittest" })
public class GroovyRestletTest {

    public void testAccessGlobalVariables() {
        final GroovyRestlet fixture = new GroovyRestlet();
        final Object result = fixture.build(new File(
                "./src/test/resources/test/AccessGlobalVariables.groovy")
                .toURI());
        assert result == null;
    }

    public void testBuildEmpty() {
        final GroovyRestlet fixture = new GroovyRestlet();
        final Object result = fixture.build(new File(
                "./src/test/resources/test/empty.groovy").toURI());
        assert result == null;
    }

    public void testConstructor() {
        final GroovyRestlet fixture = new GroovyRestlet();
        assert fixture.getBuilder() != null;
    }

    public void testGlobalVariable() {
        final GroovyRestlet fixture = new GroovyRestlet();
        final Object result = fixture.build(new File(
                "./src/test/resources/test/GlobalVariable.groovy").toURI());
        assert result == null;
    }

    public void testWithSpringContext() {
        final GroovyRestlet fixture = new GroovyRestlet(
                new ClassPathXmlApplicationContext("./testSpringContext.xml"));
        assert fixture.getBuilder() != null;
    }

}
