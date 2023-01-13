package io.github.j0b10.mad.myenergy.model.evcharger.dataQueries;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class SearchQuery {
    public final Instant dateTimeBegin;
    public final Instant dateTimeEnd;
    public final List<SearchQueryItem> queryItems;

    public SearchQuery(List<SearchQueryItem> queryItems, Instant dateTimeBegin, Instant dateTimeEnd) {
        this.dateTimeBegin = dateTimeBegin;
        this.dateTimeEnd = dateTimeEnd;
        this.queryItems = queryItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchQuery that = (SearchQuery) o;
        return Objects.equals(dateTimeBegin, that.dateTimeBegin)
                && Objects.equals(dateTimeEnd, that.dateTimeEnd)
                && Objects.equals(queryItems, that.queryItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTimeBegin, dateTimeEnd, queryItems);
    }

    @Override
    @NonNull
    public String toString() {
        return "SearchQuery{" +
                "dateTimeBegin=" + dateTimeBegin +
                ", dateTimeEnd=" + dateTimeEnd +
                ", queryItems=" + queryItems +
                '}';
    }
}
