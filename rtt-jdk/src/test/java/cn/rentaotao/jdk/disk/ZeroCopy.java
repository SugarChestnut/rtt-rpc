package cn.rentaotao.jdk.disk;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author rtt
 * @date 2024/8/13 16:33
 */
public class ZeroCopy {

    public static void normalCopy(File source, File target) throws IOException {
        /*
            常规拷贝

            数据拷贝：磁盘 -> 内核缓冲区 -> 用户缓冲区 -> socket缓冲区 -> 网卡
            状态切换：用户态 -> 内核态 -> 用户态 -> 内核态
         */
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(target);
        fis.transferTo(fos);
        fos.close();
        fis.close();
    }

    public static void useDirectBuf(File source, File target) throws IOException {
        /*
            使用直接内存进行内存映射，减少一次内核缓冲区到用户缓冲区的拷贝

            数据拷贝：磁盘 -> 内核缓冲区 -> socket缓冲区 -> 网卡
            状态切换：用户态 -> 内核态 -> 用户态 -> 内核态
         */
        FileChannel sc = FileChannel.open(Path.of(source.toURI()), StandardOpenOption.READ);
        FileChannel tc = FileChannel.open(Path.of(target.toURI()), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        long remaining = sc.size();
        long bufSize = 1024 * 1024;
        long offset = sc.position();
        MappedByteBuffer mapped;
        while (remaining > 0) {
            if (remaining <= bufSize) {
                mapped = sc.map(FileChannel.MapMode.READ_ONLY, offset, remaining);
                tc.write(mapped);
                break;
            } else {
                mapped = sc.map(FileChannel.MapMode.READ_ONLY, offset, bufSize);
                tc.write(mapped);
                offset += bufSize;
                remaining -= bufSize;
            }
        }
        tc.close();
        sc.close();
    }

    public static void sendFile(File source, File target) throws Exception {
        /*
            使用 channel 的 transfer 方法，能直接将内存缓冲区的数据输出到目标

            数据拷贝：磁盘 -> 内核缓冲区 -> 网卡
            状态切换：用户态 -> 内核态
         */
        FileChannel sc = FileChannel.open(Path.of(source.toURI()), StandardOpenOption.READ);
        FileChannel tc = FileChannel.open(Path.of(target.toURI()), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        sc.transferTo(sc.position(), sc.size(), tc);
        tc.close();
        sc.close();
    }

    public static void main(String[] args) throws IOException {
        File source = new File("/Users/rentaotao/Study/code/rtt-rpc/test.txt");
        File target = new File("/Users/rentaotao/Study/code/rtt-rpc/copy.txt");
        useDirectBuf(source, target);
    }
}
