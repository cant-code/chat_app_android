package com.damnation.etachat.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StringListTypeConvertor {

    @TypeConverter
    public static List<String> stringToStringList(String data) {
        Gson gson = new Gson();
        if(data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>(){}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String stringObjectListToString(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
