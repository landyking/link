package com.github.landyking.link.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.landyking.link.ValueBag;

import java.io.IOException;

/**
 * Created by landy on 2018/8/1.
 */
public class ValueBagSerializer extends JsonSerializer<ValueBag> {

    @Override
    public void serialize(ValueBag resultItem, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(resultItem.getFinalValue());
    }
}