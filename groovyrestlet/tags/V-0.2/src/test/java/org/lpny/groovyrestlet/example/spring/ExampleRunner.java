/**
 * 
 */
package org.lpny.groovyrestlet.example.spring;

import java.io.File;

import org.lpny.groovyrestlet.GroovyRestlet;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class ExampleRunner {
    private static final String ROOT = "./src/test/groovy/org/lpny/groovyrestlet/examples/spring/";

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // TODO Auto-generated method stub

    }

    private GroovyRestlet fixture;

    @Test(groups = { "examples" })
    public void runEx1() {
        run("Ex1.groovy");
    }

    @Test(groups = { "examples" })
    public void runEx2() {
        run("Ex2.groovy");
    }

    @BeforeClass(groups = { "examples" })
    public void setup() {
        fixture = new GroovyRestlet(new ClassPathXmlApplicationContext(
                "./testSpringContext.xml"));
    }

    private void run(final String name) {
        fixture.build(new File(ROOT, name).toURI());
    }

}
