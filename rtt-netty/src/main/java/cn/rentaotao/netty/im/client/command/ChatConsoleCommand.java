package cn.rentaotao.netty.im.client.command;

import java.util.Scanner;

/**
 * @author rtt
 * @create 2021/3/30 13:37
 */
public class ChatConsoleCommand implements BaseCommand {

    public static final String KEY = "2";

    private String toUserId;

    private String message;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "聊天";
    }

    @Override
    public void exec(Scanner scanner) {
        System.out.println("请输入聊天信息：");
        String[] info = null;
        for (;;) {
            String str = scanner.next();
            if (str != null) {
                info = str.split("@");
                if (info.length == 2) {
                    break;
                }
            }
        }
        toUserId = info[0];
        message = info[1];
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getMessage() {
        return message;
    }
}
