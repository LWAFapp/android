package com.Zakovskiy.lwaf.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonUtils {
    private static ObjectMapper mapper;
    private static JsonFactory factory;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        mapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        factory = mapper.getFactory();
    }

    public static <T> T convertJsonStringToObject(String str, Class<T> cls) {
        try {
            return mapper.readValue(str, cls);
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            return null;
        }
    }

    public static <T> T convertJsonStringToObject(String str, String str2, Class<T> cls) {
        try {
            return mapper.treeToValue(convertJsonStringToJsonNode(str).get(str2), cls);
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            return null;
        }
    }

    public static <T> T convertJsonNodeToObject(JsonNode jsonNode, Class<T> cls) {
        try {
            return mapper.treeToValue(jsonNode, cls);
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            return null;
        }
    }

    public static <T> List<T> convertJsonNodeToList(JsonNode jsonNode, Class<T> cls) {
        List<T> list;
        try {
            ObjectMapper objectMapper = mapper;
            list = (List) objectMapper.readerFor((JavaType) objectMapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) List.class, (Class<?>) cls)).readValue(jsonNode);
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            list = null;
        }
        return list == null ? new ArrayList() : list;
    }

    public static <T> List<T> convertJsonStringToList(String str, Class<T> cls) {
        try {
            ObjectMapper objectMapper = mapper;
            return (List) objectMapper.readValue(str, (JavaType) objectMapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) List.class, (Class<?>) cls));
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            return null;
        }
    }

    public static JsonNode convertJsonStringToJsonNode(String str) {
        JsonNode jsonNode = null;
        try {
            JsonParser createParser = factory.createParser(str);
            JsonNode jsonNode2 = (JsonNode) mapper.readTree(createParser);
            try {
                createParser.close();
                return jsonNode2;
            } catch (JsonParseException unused) {
                jsonNode = jsonNode2;
            } catch (IOException e) {
                e = e;
                jsonNode = jsonNode2;
                Logs.debug(e.getMessage());
            }
        } catch (JsonParseException unused2) {
            return jsonNode;
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            return jsonNode;
        }
        return jsonNode;
    }

    public static String convertObjectToJsonString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            Logs.debug(e.getMessage());
            return null;
        }
    }
}
