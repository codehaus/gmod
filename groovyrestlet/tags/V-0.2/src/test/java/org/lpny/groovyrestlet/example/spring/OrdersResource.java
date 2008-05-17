/**
 * 
 */
package org.lpny.groovyrestlet.example.spring;

import java.text.SimpleDateFormat;
import java.util.Date;

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
public class OrdersResource extends Resource {
    private SimpleDateFormat format;

    @Override
    public void init(final Context context, final Request request,
            final Response response) {
        super.init(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override
    public Representation represent(final Variant variant)
            throws ResourceException {
        return new StringRepresentation(format.format(new Date()));
    }

    public void setFormat(final SimpleDateFormat format) {
        this.format = format;
    }

}
