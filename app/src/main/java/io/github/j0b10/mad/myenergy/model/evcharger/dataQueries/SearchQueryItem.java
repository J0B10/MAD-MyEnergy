package io.github.j0b10.mad.myenergy.model.evcharger.dataQueries;

import androidx.annotation.NonNull;

import java.util.Objects;

public class SearchQueryItem {
    public final String componentId;
    public final String channelId;

    public SearchQueryItem(String componentId, String channelId) {
        this.componentId = componentId;
        this.channelId = channelId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchQueryItem that = (SearchQueryItem) o;
        return Objects.equals(componentId, that.componentId)
                && Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, channelId);
    }

    @Override
    @NonNull
    public String toString() {
        return "SearchQueryItem{" +
                "componentId='" + componentId + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
