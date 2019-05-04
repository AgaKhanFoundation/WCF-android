package com.android.wcf.helper.typeadapters;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateStringLongConverter implements JsonDeserializer<Long>, JsonSerializer<Long> {

    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String[] DATE_FORMATS = new String[]{
            ISO_8601,
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd",
            "EEE MMM dd HH:mm:ss z yyyy",
            "HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss aaa",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'",
            "MMM d',' yyyy H:mm:ss a"
    };


    @Override
    public Long deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context) throws JsonParseException {
        for (String format : DATE_FORMATS) {
            try {
                String dateString = jsonElement.getAsString();
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                //input date is UTC. If we do not set timeZone to UTC, SimpleDateFormat parser will treat the string to represent local time
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                return formatter.parse(dateString).getTime();
            } catch (ParseException e) {
                Log.e("DateParser", "error: " + e.getMessage());
            }
        }
        throw new JsonParseException("Unparserable date: \"" + jsonElement.getAsString()
                + "\". Supported formats: \n" + Arrays.toString(DATE_FORMATS));
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601, Locale.US);
        //input date is local, we want to convert it to UTC.
        // If we do not set timeZone to UTC, SimpleDateFormat will format it as local time
        return src == null ? null : new JsonPrimitive(formatter.format(src));
    }


}
