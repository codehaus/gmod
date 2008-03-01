/**
 * 
 */
package org.lpny.groovyrestlet.example.tutorial;

import java.io.File;

import org.lpny.groovyrestlet.GroovyRestlet;
import org.restlet.Client;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author keke
 * @reversion $Revision$
 * @version
 */
public class ExampleRunner {
    private static final String ROOT = "./src/test/groovy/org/lpny/groovyrestlet/examples/tutorials/";

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // TODO Auto-generated method stub

    }

    private GroovyRestlet fixture;

    @Test(groups = { "examples" })
    public void runPart02() {
        final Client client = (Client) fixture.build(new File(ROOT,
                "Part02.groovy").toURI());
        assert client != null;
    }

    @Test(groups = { "examples" })
    public void runPart03() {
        fixture.build(new File(ROOT, "Part03.groovy").toURI());

    }

    @Test(groups = { "examples" })
    public void runPart05() {
        fixture.build(new File(ROOT, "Part05.groovy").toURI());

    }

    @Test(groups = { "examples" })
    public void runPart06() {
        fixture.build(new File(ROOT, "Part06.groovy").toURI());

    }

    @Test(groups = { "examples" })
    public void runPart09() {
        fixture.build(new File(ROOT, "Part09.groovy").toURI());

    }

    @Test(groups = { "examples" })
    public void runPart10() {
        fixture.build(new File(ROOT, "Part10.groovy").toURI());

    }

    @Test(groups = { "examples" })
    public void runPart11() {
        fixture.build(new File(ROOT, "Part11.groovy").toURI());
    }

    @Test(groups = { "examples" })
    public void runPart12() {
        fixture.build(new File(ROOT, "Part12.groovy").toURI());

    }

    @BeforeClass(groups = { "examples" })
    public void setup() {
        fixture = new GroovyRestlet();
    }

}
