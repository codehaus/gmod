package org.lpny.gr.examples.tutorials

def client = builder.client(protocol.HTTP)
assert client!=null

client = builder.restlet(ofClass:"org.restlet.Client", 
        consArgs:[protocol.HTTP] as Object[])
assert client!=null

client