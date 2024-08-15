package cn.rentaotao.common.utils;

import java.io.Closeable;

/**
 * @author rtt
 * @date 2024/8/15 10:21
 */
public class IOUtils {

    public static void closeQuality(Closeable... objs) {
        if (objs != null) {
            for (Closeable obj : objs) {
                if (obj != null) {
                    try {
                        obj.close();
                    } catch (Exception e) {
                        // no-op
                    }
                }
            }
        }
    }
}
