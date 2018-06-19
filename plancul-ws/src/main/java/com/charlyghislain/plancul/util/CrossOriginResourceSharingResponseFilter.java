package com.charlyghislain.plancul.util;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Append CORS headers to responses.
 *
 * @author cghislai
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CrossOriginResourceSharingResponseFilter implements ContainerResponseFilter {

    @Inject
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext response) {
        String origin = requestContext.getHeaderString("Origin");

        // TODO: filter origin
        MultivaluedMap<String, Object> headerMap = response.getHeaders();
        headerMap.putSingle("Access-Control-Allow-Origin", origin);
        headerMap.putSingle("Access-Control-Allow-Credentials", "true");
    }

    public void filter(HttpServletResponse servletResponse) {
        String origin = httpServletRequest.getHeader("Origin");

        // TODO: filter origin
        servletResponse.addHeader("Access-Control-Allow-Origin", origin);
        servletResponse.addHeader("Access-Control-Allow-Credentials", "true");
    }


    public void filter(Response response) {
        String origin = httpServletRequest.getHeader("Origin");

        // TODO: filter origin
        MultivaluedMap<String, Object> headerMap = response.getHeaders();
        headerMap.putSingle("Access-Control-Allow-Origin", origin);
        headerMap.putSingle("Access-Control-Allow-Credentials", "true");
    }

}
