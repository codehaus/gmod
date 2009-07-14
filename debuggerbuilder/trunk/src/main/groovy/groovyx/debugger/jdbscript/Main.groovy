package groovyx.debugger.jdbscript

import groovyx.debugger.Debugger
import groovyx.debugger.Script
import org.codehaus.groovy.control.CompilerConfiguration

class Main {
    private jpdaSpec, script

    Main(jpdaSpec, scriptFile) {
        this.jpdaSpec = parseJpdaSpec(jpdaSpec)
        this.script = parseScript(scriptFile)
    }

    private void doMain() {
        assert script instanceof Script

        def debugger = script.run()
        assert debugger instanceof Debugger

        debugger.attach(jpdaSpec.transportName, jpdaSpec.attachArgs)
    }

    private parseJpdaSpec(spec) {
        def specParts = spec.split(":", 2)

        def parsed = [ transportName: specParts[0] ]

        parsed.attachArgs = [:]
        specParts[1].split(",").each {
            def parts = it.split("=", 2)
            parsed.attachArgs[parts[0]] = parts[1]
        }

        return parsed
    }

    private parseScript(scriptFile) {
        def cc = new CompilerConfiguration()
        cc.scriptBaseClass = Script.name

        def gcl = new GroovyClassLoader(this.class.classLoader, cc)
        return gcl.parseClass(new File(scriptFile)).newInstance()
    }

    static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: jdbscript JPDA_INFO SCRIPT")
            System.exit(1)
        }
        new Main(args[0], args[1]).doMain()
    }
}
