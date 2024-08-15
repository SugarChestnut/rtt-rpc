package cn.rentaotao.netty.im.bean;


import cn.rentaotao.common.utils.RandomUtils;
import lombok.Data;

import java.util.UUID;

/**
 * @author rtt
 * @create 2021/3/29 10:49
 */
@Data
public class User {

    String uid = String.valueOf(RandomUtils.getRandomInt(100));

    String devId = UUID.randomUUID().toString();

    String token = UUID.randomUUID().toString();

    String nickName = "rtt";

    PlatType platform = PlatType.WINDOWS;

    private String sessionId;

    public void setPlatform(int platform) {
        PlatType[] platTypes = PlatType.values();
        for (PlatType platType : platTypes) {
            if (platType.ordinal() == platform) {
                this.platform = platType;
            }
        }
    }

    public static User fromMsg(ImOuterClass.Im.LoginRequest loginRequest) {
        User user = new User();
        user.uid = loginRequest.getUid();
        user.devId = loginRequest.getDeviceId();
        user.token = loginRequest.getToken();
        user.setPlatform(loginRequest.getPlatform());

        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", devId='" + devId + '\'' +
                ", token='" + token + '\'' +
                ", nickName='" + nickName + '\'' +
                ", platform=" + platform +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    public boolean same(User user) {
        if (user == null) {
            return false;
        }
        return uid.equals(user.getUid()) && platform.equals(user.platform) ;
    }

    public enum PlatType {
        WINDOWS,
        MAC,
        ANDROID,
        IOS
    }
}
