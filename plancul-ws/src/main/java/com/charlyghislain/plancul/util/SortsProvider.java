package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.api.request.WsSearchQueryParams;
import com.charlyghislain.plancul.domain.api.request.sort.WsSortOrder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SortsProvider {


    @Context
    private UriInfo uriInfo;

    @Produces
    @RequestScoped
    public List<UntypedSort> getSorts() {
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        List<String> sortsValues = queryParameters.getOrDefault(WsSearchQueryParams.sorts.name(), Collections.emptyList());
        return sortsValues.stream()
                .map(this::parseSort)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<UntypedSort> parseSort(String paramValue) {
        String[] splitted = paramValue.split(":");
        if (splitted.length != 2) {
            return Optional.empty();
        }
        String fieldName = splitted[0].toUpperCase();
        boolean ascending = splitted[1].equalsIgnoreCase(WsSortOrder.ASC.name());

        UntypedSort untypedSort = new UntypedSort(fieldName, ascending);
        return Optional.of(untypedSort);
    }
}
