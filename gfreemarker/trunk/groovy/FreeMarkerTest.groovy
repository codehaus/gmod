import groovy.text.freemarker.FreeMarkerTemplateEngine

/**
 * Created by IntelliJ IDEA.
 * User: cedric
 * Date: 2 août 2007
 * Time: 20:09:19
 */
def tpl = '''
Hello, ${user.name}
<@groovy plugin="urlencoder" mode=user>this is a test ${user.name}</@groovy>'''
def engine = new FreeMarkerTemplateEngine("plugins")
def binding = ["user" : ["name":"cedric"]]
println engine.createTemplate(tpl).make(binding)
