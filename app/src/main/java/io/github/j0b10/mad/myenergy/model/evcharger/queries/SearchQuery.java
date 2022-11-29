package io.github.j0b10.mad.myenergy.model.evcharger.queries;

import java.time.Instant;
import java.util.List;

public class SearchQuery {
    public final Instant dateTimeBegin;
    public final Instant dateTimeEnd;
    public final List<SearchQueryItem> queryItems;

    public SearchQuery(List<SearchQueryItem> queryItems, Instant dateTimeBegin, Instant dateTimeEnd) {
        this.dateTimeBegin = dateTimeBegin;
        this.dateTimeEnd = dateTimeEnd;
        this.queryItems = queryItems;
    }
}
