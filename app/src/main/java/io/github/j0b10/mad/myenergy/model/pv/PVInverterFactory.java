package io.github.j0b10.mad.myenergy.model.pv;

public interface PVInverterFactory<T extends PVInverter> {
    T deserialize(String string);

    String serialize(T pvInverter);
}
