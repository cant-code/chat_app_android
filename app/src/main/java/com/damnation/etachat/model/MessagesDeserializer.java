package com.damnation.etachat.model;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessagesDeserializer implements JsonDeserializer<Messages> {
    @Override
    public Messages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        JsonObject jsonObject = json.getAsJsonObject();

        return new Messages(
                jsonObject.get("_id").getAsString(),
                jsonObject.get("from").getAsString(),
                jsonObject.get("to") == null ? null : jsonObject.get("to").getAsString(),
                jsonObject.get("date").getAsString(),
                jsonObject.get("body").getAsString(),
                gson.fromJson(jsonObject.getAsJsonArray("fromObj").get(0), User.class)
        );
    }
}
