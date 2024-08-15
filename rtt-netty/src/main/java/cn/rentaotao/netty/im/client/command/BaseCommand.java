package cn.rentaotao.netty.im.client.command;

import java.util.Scanner;

/**
 * @author rtt
 * @create 2021/3/29 10:07
 */
public interface BaseCommand {

    /**
     * 获取命令的 key
     *
     * @return ket
     */
    String getKey();

    /**
     * 获取命令的提示信息
     *
     * @return 提示信息
     */
    String getTip();

    /**
     * 从控制台中提取业务
     *
     * @param scanner 控制台信息获取
     */
    void exec(Scanner scanner);

}
