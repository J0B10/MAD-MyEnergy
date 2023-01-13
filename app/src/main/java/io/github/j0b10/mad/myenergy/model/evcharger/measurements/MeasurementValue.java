package io.github.j0b10.mad.myenergy.model.evcharger.measurements;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Objects;

public class MeasurementValue {

    public final Instant time;
    public final double value;

    public MeasurementValue(Instant time, double value) {
        this.time = time;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurementValue that = (MeasurementValue) o;
        return Double.compare(that.value, value) == 0
                && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "MeasurementValue{" +
                "time=" + time +
                ", value=" + value +
                '}';
    }
}
