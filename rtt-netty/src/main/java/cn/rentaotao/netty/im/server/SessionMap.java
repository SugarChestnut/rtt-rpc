package cn.rentaotao.netty.im.server;


import cn.rentaotao.netty.im.bean.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author rtt
 * @create 2021/3/31 14:21
 */
public class SessionMap {

    private SessionMap() {
    }

    private static SessionMap instance = new SessionMap();

    private ConcurrentHashMap<String, ServerSession> map = new ConcurrentHashMap<>(8);

    public static SessionMap inst() {
        return instance;
    }

    /**
     * 添加用户 session
     *
     * @param session
     */
    public void addSession(ServerSession session) {
        map.put(session.getSessionId(), session);
        System.out.println("用户登录:id=" + session.getUser().getUid() + ", 在线总数:" + map.size());
    }

    /**
     * 根据 sessionId 获得 session
     *
     * @param sessionId
     * @return
     */
    public ServerSession getSession(String sessionId) {
        return map.get(sessionId);
    }

    /**
     * 根据用户 id 获取 session
     *
     * @param userId
     * @return
     */
    public List<ServerSession> getSessionByUserId(String userId) {
        return map.values().stream().filter(s -> {
            if (s == null) {
                return false;
            }
            return s.getUser().getUid().equals(userId);
        }).collect(Collectors.toList());
    }

    /**
     * 移除登录信息
     *
     * @param sessionId
     */
    public void removeSession(String sessionId) {
        ServerSession session = map.remove(sessionId);
        if (session != null) {
            System.out.println("用户下线:id=" + session.getUser().getUid() + ", 在线总数:" + map.size());
        }
    }

    /**
     * 查看用户是否登录
     *
     * @param user
     * @return
     */
    public boolean hasLogin(User user) {
        for (Map.Entry<String, ServerSession> entry : map.entrySet()) {
            User u = entry.getValue().getUser();
            if (u.same(user)) {
                return true;
            }
        }
        return false;
    }
}
