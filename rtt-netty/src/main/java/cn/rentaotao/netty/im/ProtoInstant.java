package cn.rentaotao.netty.im;
/**
 * @author rtt
 * @create 2021/3/30 14:19
 */
public class ProtoInstant {

    public static final short MAGIC_CODE = 0x86;

    public static final short VERSION_CODE = 0x01;

    public interface Platform {
        int WINDOWS = 1;
        int MAC = 2;
        int ANDROID = 3;
        int IOS = 4;
        int WEB = 5;
        int UNKNOWN = 6;
    }

    public enum ResultCodeEnum {
        /**
         * 成功
         */
        SUCCESS(0, "成功"),
        /**
         * 登录失败
         */
        AUTH_FAILED(1, "登录失败"),
        /**
         * 未认证
         */
        NO_TOKEN(2, "未认证"),
        /**
         * 未知错误
         */
        UNKNOWN_ERROR(3, "未知错误");

        private final Integer code;

        private final String desc;

        ResultCodeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
