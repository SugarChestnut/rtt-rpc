package cn.rentaotao.netty;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存连接
 *
 * @author rtt
 * @date 2024/11/21 16:43
 */
public class ConnectChannelPool {

    private final ConcurrentHashMap<String, Channel> cache = new ConcurrentHashMap<>();

}
