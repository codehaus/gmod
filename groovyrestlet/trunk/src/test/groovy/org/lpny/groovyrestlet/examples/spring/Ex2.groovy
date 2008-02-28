package org.lpny.gr.examples.spring

def comp = builder.component(){
    application(uri:""){
        router{
            resource(uri:"/orders", ofClass:"org.lpny.gr.example.spring.OrdersResource")
        }
    }
}

comp.servers.add(protocol.HTTP, 8182)
comp.start();

def client = builder.client(protocol:protocol.HTTP)

def resp = client.get("http://localhost:8182/orders")
assert resp.status == status.SUCCESS_OK
//assert resp.entity.text == "Hello test"

comp.stop()