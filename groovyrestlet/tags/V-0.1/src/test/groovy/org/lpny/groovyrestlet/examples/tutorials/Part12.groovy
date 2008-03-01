package org.lpny.gr.examples.tutorials
import org.restlet.resource.*
import org.restlet.data.*
import org.restlet.*

class OrdersResource extends Resource{
    public void init(Context context, Request request, Response response){
        super.init(context, request, response)
        variants.add(new Variant(MediaType.TEXT_PLAIN))
    }
    public Representation represent(Variant variant) throws ResourceException{
        return new StringRepresentation("Orders List");
    }
}

def com = builder.component{
    current.servers.add(protocol.HTTP, 8182)
    
    application(uri:""){
        router{
            resource("/users/{user}",
                    init:{ctx, req, resp, self->                           
                        self.getVariants().add(new Variant(mediaType.TEXT_PLAIN))
                    },
                    represent:{variant, self->                        
                        return new StringRepresentation(
                                "Account of user \"${self.request.attributes.get('user')}".toString(),
                                mediaType.TEXT_PLAIN);
                    })
             resource("/users/{user}/orders", ofClass:OrdersResource)
        }
    }
}
com.start()

def client = builder.client(protocol:protocol.HTTP)

def resp = client.get("http://localhost:8182/users/test")
assert resp.status == status.SUCCESS_OK
resp = client.get("http://localhost:8182/users/test/orders")
assert resp.status == status.SUCCESS_OK

com.stop()