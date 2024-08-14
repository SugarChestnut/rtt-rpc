package cn.rentaotao.jdk.disk;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author rtt
 * @date 2024/8/13 09:22
 */
public class FileStreamReadTest {

    public static void main(String[] args) throws Exception {



    }

    public static InputStream getStream(String fileName) {
        return FileStreamReadTest.class.getResourceAsStream("/ft.txt");
    }

    public static File getFile(String fileName) {
        String path = Objects.requireNonNull(FileStreamReadTest.class.getResource("")).getPath();
        System.out.println(path);
        // 中文解码
        String np = URLDecoder.decode(path + "fileName", StandardCharsets.UTF_8);
        System.out.println(np);
        return new File(np);
    }
}
