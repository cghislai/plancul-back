package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsRef;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchResultConverter {

    public <A extends DomainEntity, B extends WsDomainEntity> WsSearchResult<B>
    convertSearchResults(SearchResult<A> searchResults, ToWsDomainObjectConverter<A, B> converter) {
        List<WsRef<B>> wsRefList = searchResults.getList().stream()
                .map(converter::reference)
                .collect(Collectors.toList());
        long totalCount = searchResults.getTotalCount();

        WsSearchResult<B> wsSearchResult = new WsSearchResult<>();
        wsSearchResult.setList(wsRefList);
        wsSearchResult.setCount(totalCount);
        return wsSearchResult;
    }
}
