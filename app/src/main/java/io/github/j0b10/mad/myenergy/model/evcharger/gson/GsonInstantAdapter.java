package io.github.j0b10.mad.myenergy.model.evcharger.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class GsonInstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Instant.parse(json.getAsString());
        } catch (UnsupportedOperationException | DateTimeParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
