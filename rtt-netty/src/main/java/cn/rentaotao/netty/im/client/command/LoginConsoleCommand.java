package cn.rentaotao.netty.im.client.command;

import java.util.Scanner;

/**
 * @author rtt
 * @create 2021/3/29 10:11
 */
public class LoginConsoleCommand implements BaseCommand{

    public static final String KEY = "1";

    private String username;

    private String password;

    @Override
    public void exec(Scanner scanner) {
        System.out.println("输入用户信息（id@password）：");
        for (;;) {
            String inputStr = scanner.next();
            String[] info = inputStr.split("@");
            if (info.length != 2) {
                System.out.println("格式错误！");
            } else {
                username = info[0];
                password = info[1];
                break;
            }
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "登录";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
