package cn.rentaotao.netty.im.client.command;

import java.util.Scanner;

/**
 * @author rtt
 * @create 2021/3/30 13:46
 */
public class LogoutConsoleCommand implements BaseCommand{

    public static final String KEY = "9";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "退出";
    }

    @Override
    public void exec(Scanner scanner) {
        System.out.println("执行退出程序");
    }
}
