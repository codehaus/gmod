package org.lpny.gr.examples.tutorials

def com = builder.component{
    current.servers.add(protocol.HTTP, 8182)
    application(uri:""){
        def router = router{
            def target = "http://www.google.com/search?q={keywords}"
            redirector(uri:"/search",
                    targetTemplate:target, mode:redirectorMode.MODE_CLIENT_TEMPORARY,
                    postAttach:{route->
                        route.extractQuery("keywords","kwd",true)
            })
        }        
    }
}
com.start()

def client = builder.client(protocol.HTTP)
def resp = client.get("http://localhost:8182/search?kwd=Restlet")
assert resp.status == status.REDIRECTION_TEMPORARY
assert resp.locationRef.toString().equals("http://www.google.com/search?q=Restlet") : "Location ${resp.locationRef.toString()}"

com.stop()