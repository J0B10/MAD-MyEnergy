package io.github.j0b10.mad.myenergy.model.evcharger.parameters;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class Parameter {
    public final String channelId;
    public final Boolean editable;
    public final Instant timestamp;
    public final String value;
    public final String[] possibleValues;

    public Parameter(String channelId, Boolean editable, Instant timestamp, String value, String[] possibleValues) {
        this.channelId = channelId;
        this.editable = editable;
        this.timestamp = timestamp;
        this.value = value;
        this.possibleValues = possibleValues;
    }

    public Parameter(String channelId, String value, Instant timestamp) {
        this(channelId, null, timestamp, value, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(channelId, parameter.channelId)
                && Objects.equals(editable, parameter.editable)
                && Objects.equals(timestamp, parameter.timestamp)
                && Objects.equals(value, parameter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, editable, timestamp, value);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "Parameter{" +
                "channelId='" + channelId + '\'' +
                ", editable=" + editable +
                ", timestamp=" + timestamp +
                ", value='" + value + '\'' +
                ", possibleValues=" + Arrays.toString(possibleValues) +
                '}';
    }
}
