package io.github.j0b10.mad.myenergy.model.evcharger.parameters;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class DeviceParameters {

    public final String componentId;
    public final List<Parameter> values;

    public DeviceParameters(String componentId, List<Parameter> values) {
        this.componentId = componentId;
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceParameters that = (DeviceParameters) o;
        return Objects.equals(componentId, that.componentId) && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, values);
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceParameters{" +
                "componentId='" + componentId + '\'' +
                ", values=" + values +
                '}';
    }
}
