package io.github.j0b10.mad.myenergy.model.evcharger.parameters;

import java.util.List;
import java.util.Objects;

public class ParameterPutQuery {
    public final List<Parameter> values;

    public ParameterPutQuery(List<Parameter> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterPutQuery that = (ParameterPutQuery) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "ParameterPutQuery{" +
                "values=" + values +
                '}';
    }
}
