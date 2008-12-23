package groovyx.net.http

import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import org.junit.Test
import java.lang.AssertionError
class HTTPBuilderTest {
	
	@Test public void testGET() {
		def http = new HTTPBuilder('http://www.google.com')
		http.get( path:'/search', query:[q:'Groovy'] ) { resp, reader ->
			println "response status: ${resp.statusLine}"
			
			println 'Response data: -----'
			System.out << reader
			println '\n--------------------'
		}
	}
	
	/* Well, Google doesn't like a POST request.  This may have to wait until I 
	 * have a proper test framework set up.  Maybe Jetty?  Jetty + RESTlet??
	 * Groovy-XMLRPC does testing against a localhost server too, maybe that 
	 * would work...
	 */
	//@Test 
	public void testPOST() {
		def http = new HTTPBuilder('http://www.google.com')
		http.post( contentType:HTML, path:'/search', body:[q:'Groovy'] ) { resp, reader ->
			println "response status: ${resp.statusLine}"
			
			println 'Response data: -----'
			System.out << reader
			println '\n--------------------'
		}
	}
	
	@Test public void testRequest() {
		def http = new HTTPBuilder()

//		 defatult response handlers.
		http.handler.'401' = { resp ->
			println 'access denied'
		}

		http.handler.failure = { resp ->
			println "Unexpected failure: ${resp.statusLine}"
		}

//		 optional default URL for all actions:
		http.URL = 'http://www.google.com' 

		http.request(GET,TEXT) { req ->
			response.success = { resp, stream ->
				println 'my response handler!'
				assert resp.statusLine.statusCode == 200
				println resp.statusLine
				//System.out << stream // print response stream
			}
		}	
	}
	
	@Test public void test404() {
		new HTTPBuilder().request('http://www.google.com',GET,TEXT) {
			url.path = '/asdfg/asasdfs' // should produce 404
			response.'404' = {
				println 'got expected 404!'
			}
			response.success = {
				assert false // should not have succeeded
			}
		}
	}
	
	/* http://googlesystem.blogspot.com/2008/04/google-search-rest-api.html
	 * http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=Earth%20Day
	 */
	@Test public void testJSON() {
		
		def builder = new HTTPBuilder()
		
//		builder.parser.'text/javascript' = builder.parsers."$JSON"
		
		builder.request('http://ajax.googleapis.com',GET,JSON) {
			url.path = '/ajax/services/search/web'
			url.query = [ v:'1.0', q: 'Calvin and Hobbes' ]
			//UA header required to get Google to GZIP response:
			headers.'User-Agent' = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.0.4) Gecko/2008111319 Ubuntu/8.10 (intrepid) Firefox/3.0.4"
			response.success = { resp, json ->
				assert resp.entity.contentEncoding.value == 'gzip'
				assert json.size() == 3
				//println json.responseData
				println "Query response: "
				json.responseData.results.each { 
					println "  ${it.titleNoFormatting} : ${it.visibleUrl}"
				}
			}
		}
	}
	
	@Test public void testAuth() {
		def http = new HTTPBuilder( 'http://test.webdav.org' )
		
		/* The path issues a 404, but it does an auth challenge first. */
		http.handler.'404' = { println 'Auth successful' }
		
		http.request( GET, HTML ) {
			url.path = '/auth-digest/'
			response.failure = { "expected failure" }
			response.success = {
				throw new AssertionError("request should have failed.")
			}
		}
		
		http.auth.basic( 'user2', 'user2' )

		http.request( GET, HTML ) {
			url.path = '/auth-digest/'
		}
		
		http.request( GET, HTML ) {
			url.path = '/auth-basic/'
		}
	}
}


