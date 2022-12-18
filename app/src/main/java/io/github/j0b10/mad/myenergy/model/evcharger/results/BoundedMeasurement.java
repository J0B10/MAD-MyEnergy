package io.github.j0b10.mad.myenergy.model.evcharger.results;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class BoundedMeasurement extends Measurement {

    public final double max;
    public final double min;

    public BoundedMeasurement(String channelId, String componentId, double max, double min, List<MeasurementValue> values) {
        super(channelId, componentId, values);
        this.max = max;
        this.min = min;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BoundedMeasurement that = (BoundedMeasurement) o;
        return Double.compare(that.max, max) == 0 && Double.compare(that.min, min) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), max, min);
    }

    @NonNull
    @Override
    public String toString() {
        return "BoundedMeasurement{" +
                "channelId='" + channelId + '\'' +
                ", componentId='" + componentId + '\'' +
                ", max=" + max +
                ", min=" + min +
                ", values=" + values +
                '}';
    }
}
