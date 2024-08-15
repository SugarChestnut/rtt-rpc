package cn.rentaotao.netty.im.client;

import cn.rentaotao.netty.im.client.builder.ChatBuilder;
import cn.rentaotao.netty.im.client.builder.LoginBuilder;
import cn.rentaotao.netty.im.client.command.*;
import cn.rentaotao.netty.im.client.sender.ChatSender;
import cn.rentaotao.netty.im.client.sender.LoginSender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @create 2021/3/30 09:54
 */
public class ClientController {

    private final MenuConsoleCommand menuConsoleCommand = new MenuConsoleCommand();

    private final LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();

    private final LogoutConsoleCommand logoutConsoleCommand = new LogoutConsoleCommand();

    private final ChatConsoleCommand chatConsoleCommand = new ChatConsoleCommand();

    private LoginSender loginSender;

    private ChatSender chatSender;

    private ClientSession session;

    private Map<String, BaseCommand> commandMap;

    private final ImClient imClient = new ImClient(new ClientConnectListener());

    private volatile boolean connectFlag = false;

    public ClientController() {
        initAllCommand();
    }

    public void start() {
        Thread.currentThread().setName("命令线程");

        for (;;) {
            while (!connectFlag) {
                startConnectServer();
                waitCommandThread();
            }

            while (session != null) {
                Scanner scanner = new Scanner(System.in);
                menuConsoleCommand.exec(scanner);
                String commandKey = menuConsoleCommand.getInputCommand();
                BaseCommand command = commandMap.get(commandKey);
                if (command == null) {
                    System.out.println("命令错误！");
                    continue;
                }

                switch (commandKey) {
                    case ChatConsoleCommand.KEY:
                        chatConsoleCommand.exec(scanner);
                        startSendChat((ChatConsoleCommand) command);
                        break;
                    case LoginConsoleCommand.KEY:
                        loginConsoleCommand.exec(scanner);
                        startLogIn((LoginConsoleCommand) command);
                        break;
                    case LogoutConsoleCommand.KEY:
                        logoutConsoleCommand.exec(scanner);
                        startLogout((LogoutConsoleCommand) command);
                        return;
                    default:
                        break;
                }
            }
        }
    }

    public void startSendChat(ChatConsoleCommand command) {
        String message = command.getMessage();
        String toUserId = command.getToUserId();

        chatSender.send(new ChatBuilder(session, toUserId, message).build());
    }

    public void startLogout(LogoutConsoleCommand command) {
        session.close();
        imClient.close();
    }

    public void startLogIn(LoginConsoleCommand command) {
        if (isLogin()) {
            System.out.println("请不要重复登录！");
            return;
        }
        session.setUser(command.getUsername(), command.getPassword());

        loginSender.send(new LoginBuilder(session).build());
    }

    public void initAllCommand() {
        Map<String, BaseCommand> map = new HashMap<>(8);
        map.put(menuConsoleCommand.getKey(), menuConsoleCommand);
        map.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        map.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        map.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);
        commandMap = map;
        menuConsoleCommand.setAllCommand(map);
    }

    public void startConnectServer() {
        imClient.connect();
    }

    public synchronized void waitCommandThread() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyCommandThread() {
        this.notifyAll();
    }

    public boolean isLogin() {
        if (session != null) {
            return session.isLogin();
        }

        return false;
    }

     class ClientConnectListener implements GenericFutureListener<ChannelFuture> {

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            EventLoop eventLoop = future.channel().eventLoop();
            if (future.isSuccess()) {
                connectFlag = true;

                System.out.println("服务器连接成功！");

                Channel channel = future.channel();

                session = new ClientSession(channel);
                session.setConnected(true);

                loginSender = new LoginSender(session);
                chatSender = new ChatSender(session);

                channel.closeFuture().addListener((ChannelFuture f) -> {

                    System.out.println("服务器断开连接");

                    Channel c = f.channel();
                    ClientSession s = c.attr(ClientSession.SESSION_KEY).get();
                    s.close();

                    notifyCommandThread();
                });

                notifyCommandThread();
            } else {

                System.out.println("10秒后重新连接");

                eventLoop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        imClient.connect();
                    }
                }, 10, TimeUnit.SECONDS);

                connectFlag = false;
            }
        }
    }
}
