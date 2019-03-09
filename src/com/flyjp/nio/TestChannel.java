package com.flyjp.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、通道（Channel）：用于源节点与目标节点的连接。在Java NIO中负责缓冲区中数据的传输。Channel 本身不存储数据，
 *                  因此需要配合缓冲区进行传输。
 * 二、通过的主要实现类
 *      java.nio.channnels.channnel 接口：
 *          |--FileChannel
 *          |--SocketChannel
 *          |--ServerSocketChannel
 *          |--DatagramChannel
 *  三、获取通道
 *  1.Java针对支持通道的类提供了getChannel()方法
 *      本地IO:
 *      FileInputStream/FileOutputStream
 *      RandomAccessFile
 *      网络IO:
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *   2.在JDK1.7中的NIO.2针对各个通道提供了静态方法open()
 *   3.在JDk1.7中的NIo.2的Files工具类的newByteChannel()
 */
public class TestChannel {

    //通道之间的数据传输(直接缓冲区)
    @Test
    public void test3(){
        long strat = System.currentTimeMillis();
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = FileChannel.open(Paths.get("1.jpg"),
                    StandardOpenOption.READ);
            out = FileChannel.open(Paths.get("3.jpg"),
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
           // in.transferTo(0,in.size(),out);
            out.transferFrom(in,0,in.size());
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    //使用直接缓冲区完成文件的复制（内存映射文件）
    @Test
    public void test2(){
        long strat = System.currentTimeMillis();
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = FileChannel.open(Paths.get("1.jpg"),
                    StandardOpenOption.READ);
            out = FileChannel.open(Paths.get("3.jpg"),
                    StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            //内存映射文件 缓冲区在物理内存中
            MappedByteBuffer inMappedBuf = in.map(FileChannel.MapMode.READ_ONLY,0,in.size());
            MappedByteBuffer outMappedBuf = out.map(FileChannel.MapMode.READ_WRITE,0,in.size());

            //直接对缓冲区进行数据的读写操作
            byte[] dst = new byte[inMappedBuf.limit()];
            inMappedBuf.get(dst);
            outMappedBuf.put(dst);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗费时间："+(end-strat));
    }

    //利用通道完成文件的复制
    @Test
    public void test1(){
        long start = System.currentTimeMillis();

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");

            //1.获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //2.分配制定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //3.将通道中的数据存入缓冲区
            while (inChannel.read(buf) != -1) {
                buf.flip();//切换到读取数据的模式
                //4.将缓冲区中的数据写入通道
                outChannel.write(buf);
                buf.clear();//清空缓冲区
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(outChannel != null){
                    outChannel.close();
                }
                if(inChannel != null){
                    inChannel.close();
                }
                if(fos != null){
                    fos.close();
                }
                if(fos != null){
                    fis.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗费时间："+(end-start));
    }
}
