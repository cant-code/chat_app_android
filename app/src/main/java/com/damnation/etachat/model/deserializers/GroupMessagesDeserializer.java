package com.damnation.etachat.model.deserializers;

import com.damnation.etachat.model.GroupMessages;
import com.damnation.etachat.model.User;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GroupMessagesDeserializer implements JsonDeserializer<GroupMessages> {
    @Override
    public GroupMessages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new GroupMessages(
                jsonObject.get("_id").getAsString(),
                jsonObject.get("from").getAsString(),
                jsonObject.get("group").getAsString(),
                jsonObject.get("date").getAsString(),
                jsonObject.get("body").getAsString(),
                new Gson().fromJson(jsonObject.getAsJsonArray("fromObj").get(0), User.class)
        );
    }
}
