package com.charlyghislain.plancul.astronomy.ws.provider;

import javax.annotation.Priority;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Respond with CORS headers to OPTIONS requests.
 *
 * @author cghislai
 */
@Provider
@PreMatching
@Priority(Priorities.HEADER_DECORATOR)
public class CrossOriginResourceSharingRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        if (!HttpMethod.OPTIONS.equals(method)) {
            return;
        }

        // Origin & credential headers are appended by a response filter which will be called as well.
        Response okResponse = Response.ok()
                .header("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE")
                .header("Access-Control-Allow-Headers", "content-type, accept, accept-charset, authorization, X-Requested-With")
                .header("Access-Control-Expose-Headers", "accept-ranges, content-encoding, content-length")
                .build();
        requestContext.abortWith(okResponse);
    }
}
