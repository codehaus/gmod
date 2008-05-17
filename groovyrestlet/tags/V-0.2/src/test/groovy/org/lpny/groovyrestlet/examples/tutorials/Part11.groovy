package org.lpny.gr.examples.tutorials

def com = builder.component{
    current.servers.add(protocol.HTTP, 8182)
    
    application(uri:""){
        router{
            def guard = guard(uri:"/docs", scheme:challengeScheme.HTTP_BASIC, 
                    realm:"Restlet Tutorials")
            guard.secrets.put("scott", "tiger".toCharArray())
            guard.next = directory(root:"", autoAttach:false)
            
            restlet(uri:"/users/{user}", handle:{req,resp->
                resp.setEntity("Account of user \"${req.attributes.get('user')}\"",
                        mediaType.TEXT_PLAIN)
            })
            
            restlet(uri:"/users/{user}/orders", handle:{req, resp->
                resp.setEntity("Orders or user \"${req.attributes.get('user')}\"",
                        mediaType.TEXT_PLAIN)
            })
            
            restlet(uri:"/users/{user}/orders/{order}", handle:{req, resp->
                def attrs = req.attributes
                def message = "Order \"${attrs.get('order')}\" for User \"${attrs.get('user')}\""
                resp.setEntity(message, mediaType.TEXT_PLAIN)
            })
        }
    }
}
com.start()

def client = builder.client(protocol:protocol.HTTP)
def resp = client.get("http://localhost:8182/docs/pom.xml")
assert resp.status == status.CLIENT_ERROR_UNAUTHORIZED

resp = client.get("http://localhost:8182/users/test")
assert resp.status == status.SUCCESS_OK
assert "Account of user \"test\"" == resp.entity.text

resp = client.get("http://localhost:8182/users/test/orders")
assert resp.status == status.SUCCESS_OK
println resp.entity.text
resp = client.get("http://localhost:8182/users/test/orders/restlet")
assert resp.status == status.SUCCESS_OK
println resp.entity.text

com.stop()