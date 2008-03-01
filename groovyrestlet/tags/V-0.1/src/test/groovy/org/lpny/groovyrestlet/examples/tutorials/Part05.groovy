package org.lpny.gr.examples.tutorials

def component = builder.component{
    current.servers.add(protocol.HTTP, 8182)
    
    restlet(uri:"/trace", handle: {req, resp->
        println "To process request: ${req}"
        def message = """Resource URI: ${req.resourceRef}
Root URI : ${req.rootRef}
Routed part : ${req.resourceRef.baseRef}
Remaining part: ${req.resourceRef.remainingPart}"""
        resp.setEntity(message, mediaType.TEXT_PLAIN)
    })
}

component.start()

def client = builder.client(protocol:protocol.HTTP)

def text = client.get("http://localhost:8182/trace/abc/def?param=123").getEntity().getText()
def expected = """Resource URI: http://localhost:8182/trace/abc/def?param=123
Root URI : http://localhost:8182/trace
Routed part : http://localhost:8182/trace
Remaining part: /abc/def?param=123"""
assert text == expected

component.stop()