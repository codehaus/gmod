import org.codehaus.groovy.gfreemarker.freemarker.IGroovyFreeMarkerPlugin

class urlencoder implements IGroovyFreeMarkerPlugin {
	String transform(Map params, String content) {
		URLEncoder.encode(content);
	}
}