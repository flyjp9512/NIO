package com.flyjp.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {

    @Test
    public void test1() throws IOException {
            //1.获取管道
            Pipe pipe = Pipe.open();
            //2.将缓存区写入数据
            ByteBuffer buf = ByteBuffer.allocate(1024);
            Pipe.SinkChannel sinkChannel = pipe.sink();
            buf.put("通过单项管道发送数据".getBytes());
            buf.flip();
            sinkChannel.write(buf);
            //3.读取缓冲区中的数据
            Pipe.SourceChannel sourceChannel = pipe.source();
            buf.flip();
            int len = sourceChannel.read(buf);
            System.out.println(new String(buf.array(),0,len));
            if (sinkChannel != null) {
                sinkChannel.close();
            }
            if(sourceChannel != null){
                sourceChannel.close();
            }
        }


}
