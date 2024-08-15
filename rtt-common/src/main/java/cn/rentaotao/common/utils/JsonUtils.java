package cn.rentaotao.common.utils;

import com.google.gson.GsonBuilder;

/**
 * @author rtt
 * @create 2021/3/25 10:07
 */
public class JsonUtils {
    static GsonBuilder gb = new GsonBuilder();

    static {
        gb.disableHtmlEscaping();
    }

    public static String object2String(Object obj) {
        return gb.create().toJson(obj);
    }

    public static <T> T string2Object(String json, Class<T> tClass) {
        return gb.create().fromJson(json, tClass);
    }
}
