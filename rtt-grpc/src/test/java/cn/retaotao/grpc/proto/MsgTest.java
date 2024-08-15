package cn.retaotao.grpc.proto;

import cn.rentaotao.common.proto.Msg;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author rtt
 * @date 2024/8/15 13:26
 */
public class MsgTest {

    @Test
    public void test() throws Exception {
        Msg.Builder builder = Msg.newBuilder();
        builder.setId(100);
        builder.setContent("proto测试文本");
        Msg msg = builder.build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        /*
            outputStream.write(msg.toByteArray());
            msg.writeTo(outputStream);
        */
        msg.writeDelimitedTo(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Msg inMsg = Msg.parseDelimitedFrom(inputStream);

        System.out.println(inMsg.getId());
        System.out.println(inMsg.getContent());
    }
}
