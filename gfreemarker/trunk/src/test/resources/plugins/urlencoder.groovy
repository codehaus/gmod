import org.codehaus.groovy.gfreemarker.IGroovyFreeMarkerPlugin

class urlencoder implements IGroovyFreeMarkerPlugin {
	String transform(Map params, String content) {
		URLEncoder.encode(content);
	}
}