package io.github.j0b10.mad.myenergy.model.evcharger.results;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class Measurement {

    public final String channelId;

    public final String componentId;

    public final List<MeasurementValue> values;

    public Measurement(String channelId, String componentId, List<MeasurementValue> values) {
        this.channelId = channelId;
        this.componentId = componentId;
        this.values = List.copyOf(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return Objects.equals(channelId, that.channelId)
                && Objects.equals(componentId, that.componentId)
                && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, componentId, values);
    }

    @NonNull
    @Override
    public String toString() {
        return "Measurement{" +
                "channelId='" + channelId + '\'' +
                ", componentId='" + componentId + '\'' +
                ", values=" + values +
                '}';
    }
}
