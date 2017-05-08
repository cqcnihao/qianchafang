package com.qianchafang.framework.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Poan on 2017/5/8.
 * 实体与Json互相转换的工具类
 */
public class ObjectMapperUtils {

    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static <T> String toJSON(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final <T> T value(Object rawValue, Class<T> type) {
        return mapper.convertValue(rawValue, type);

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T fromJSON(String json, Class<? extends Collection> collectionType,
                                 Class<?> valueType) {
        if ((json == null) || (json.length() == 0)) {
            try {
                return (T) collectionType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (T) mapper.readValue(json, TypeFactory.defaultInstance()
                    .constructCollectionType(collectionType, valueType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T fromJSON(String json, Class<? extends Map> mapType, Class<?> keyType,
                                 Class<?> valueType) {
        if ((json == null) || (json.length() == 0)) {
            try {
                return (T) mapType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (T) mapper.readValue(json,
                    TypeFactory.defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}