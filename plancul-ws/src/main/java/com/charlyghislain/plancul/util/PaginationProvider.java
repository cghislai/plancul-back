package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.util.Pagination;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@ApplicationScoped
public class PaginationProvider {

    @Context
    private UriInfo uriInfo;

    @Produces
    @RequestScoped
    public Pagination getPagination() {
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        String offsetParam = queryParameters.getFirst("offset");
        String lengthParam = queryParameters.getFirst("length");

        int offsetValue = Optional.ofNullable(offsetParam)
                .map(Integer::parseInt)
                .orElse(0);
        int lengthValue = Optional.ofNullable(lengthParam)
                .map(Integer::parseInt)
                .orElse(0);

        return new Pagination(offsetValue, lengthValue);
    }
}
