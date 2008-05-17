/**
 * 
 */
package org.lpny.groovyrestlet.example.spring;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * @author keke
 * @version
 * @since
 * @revision $Revision$
 */
public class UserResource extends Resource {

    @Override
    public void init(final Context context, final Request request,
            final Response response) {
        // TODO Auto-generated method stub
        super.init(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override
    public Representation represent(final Variant variant)
            throws ResourceException {
        return new StringRepresentation("Hello "
                + getRequest().getAttributes().get("user"));
    }

}
