package org.lpny.gr.examples.tutorials
/*def restlet = builder.restlet(handle:{req, resp->
    resp.setEntity("Hello World", mediaType.TEXT_PLAIN)
})*/
//println restlet.class.superclass
def server = builder.server(protocol:protocol.HTTP,
        port:8182){
    restlet(handle:{req, resp->
        resp.setEntity("Hello World", mediaType.TEXT_PLAIN)
    })
}

server.start();

def client = builder.client(protocol:protocol.HTTP)
assert "Hello World" == client.get("http://localhost:8182").getEntity().getText()

server.stop()