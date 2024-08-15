package cn.rentaotao.netty.im.client.command;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author rtt
 * @create 2021/3/30 13:49
 */
public class MenuConsoleCommand implements BaseCommand{

    public static final String KEY = "0";

    private String allCommand;

    private String inputCommand;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "菜单";
    }

    @Override
    public void exec(Scanner scanner) {
        System.out.println(allCommand);
        System.out.println("请输入操作指令：");
        this.inputCommand = scanner.next();
    }

    public String getAllCommand() {
        return allCommand;
    }

    public void setAllCommand(Map<String, BaseCommand> map) {
        Set<Map.Entry<String, BaseCommand>> entries = map.entrySet();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, BaseCommand> entry : entries) {
            builder.append(entry.getKey());
            builder.append(" -> ");
            builder.append(entry.getValue().getTip());
            builder.append(" | ");
        }
        this.allCommand = builder.toString();
    }

    public String getInputCommand() {
        return inputCommand;
    }
}
