package org.lpny.gr.examples.tutorials
//import org.restlet.data.*

def component = builder.component{
    current.servers.add(protocol.HTTP, 8182)
    application(uri:""){
        guard(scheme:challengeScheme.HTTP_BASIC, realm:"Tutorial")
            .secrets.put("scott","tiger".toCharArray())
            
        def dir = directory(autoAttach:false, root:"")
        current.root.next=dir
    }
}
component.start()

def client = builder.client(protocol.HTTP)
def resp = client.get("http://localhost:8182/pom.xml")
assert resp.status == status.CLIENT_ERROR_UNAUTHORIZED

component.stop()