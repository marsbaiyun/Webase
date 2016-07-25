package com.seveniu.util;

import com.alibaba.fastjson.JSON;

public class Json {
    public static String toJson(Object object) {
        return JSON.toJSONString(object);
    }
}
