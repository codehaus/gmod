package org.lpny.gr.examples.tutorials

def component = builder.component{
    current.servers.add(protocol.HTTP, 8182)
    current.clients.add(protocol.FILE)
    
    application(uri:""){
        directory(root:"")
    }
}
component.start()

def client = builder.client(protocol.HTTP)

def resp = client.get("http://localhost:8182/pom.xml")
assert resp != null
assert resp.status == status.SUCCESS_OK : "Status=${resp.status}"

component.stop()