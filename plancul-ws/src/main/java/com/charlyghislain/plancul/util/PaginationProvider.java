package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.api.request.WsSearchQueryParams;
import com.charlyghislain.plancul.domain.request.Pagination;

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
        String offsetParam = queryParameters.getFirst(WsSearchQueryParams.offset.name());
        String lengthParam = queryParameters.getFirst(WsSearchQueryParams.length.name());

        int offsetValue = Optional.ofNullable(offsetParam)
                .map(Integer::parseInt)
                .orElse(0);
        int lengthValue = Optional.ofNullable(lengthParam)
                .map(Integer::parseInt)
                .orElse(0);

        return new Pagination(offsetValue, lengthValue);
    }
}
