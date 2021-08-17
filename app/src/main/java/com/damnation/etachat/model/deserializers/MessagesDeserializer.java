package com.damnation.etachat.model.deserializers;

import com.damnation.etachat.model.Messages;
import com.damnation.etachat.model.User;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MessagesDeserializer implements JsonDeserializer<Messages> {
    @Override
    public Messages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new Messages(
                jsonObject.get("_id").getAsString(),
                jsonObject.get("from").getAsString(),
                jsonObject.has("to") ? jsonObject.get("to").getAsString() : null,
                jsonObject.get("date").getAsString(),
                jsonObject.get("body").getAsString(),
                new Gson().fromJson(jsonObject.getAsJsonArray("fromObj").get(0), User.class)
        );
    }
}
