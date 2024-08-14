package cn.rentaotao.jdk.net;

import lombok.Data;

/**
 * @author rtt
 * @date 2024/8/14 10:01
 */
@Data
public class ConnectionHolder<T> {

    T connection;
    long connTime;
    long lastActive;
    String remoteAddr;
    String id;
}
