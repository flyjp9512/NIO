package com.flyjp.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 一、缓冲区（Buffer）：在 java nio 中负责数据的存取。缓冲区就是数组。用于存储不同数据类型的数据
 * 根据书类型不同（boolean 除外），提供了相应类型的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 *
 * 上述缓冲区的管理方式几乎一致，通过allocate()获取缓冲区
 *
 * 二、缓冲区存取数据的两个核心方法：
 * put():存入数据到缓冲区中
 * get():获取缓冲区中的数据
 *
 * 三、缓冲区中的四个核心属性
 * capacity:容量，表示缓冲区中最大的存储数据的容量，一旦声明不能改变。
 * limit：界限，表示缓冲区中可以操作的数据大小。（limit 后数据不能进行读写）
 * position:位置，表示缓冲区中正在操作的数据位置。
 * mark：标记，表示记录当前position的位置。可以通过reset()恢复mark的位置
 * 0 <= mark <= position <= limit <= capacity
 *
 * 五、直接缓冲区与非直接缓冲区：
 * 非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在JVM的内存中
 *      物理磁盘 read()-> 内核地址空间（OS） copy-> 用户地址空间（JVM） read()->应用程序
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 *      物理磁盘 read()-> 物理内存映射文件（完全由系统控制） read()-> 应用程序
 *      效率高但存在风险 分配消耗与销毁的资源大 不易控制
 */
public class TestBuffer {

    @Test
    public void test3(){
        //分配直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);

        System.out.println(buf.isDirect());//true


    }

    @Test
    public void test2(){
        String str = "abcdef";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());
        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println(new String(dst,0,2));//ab
        System.out.println(buf.position());//2
        //mark() :标记
        buf.mark();

        buf.get(dst,2,2);
        System.out.println(new String(dst,2,2));//cd
        System.out.println(buf.position());//4

        //reset() : 恢复到mark的位置
        buf.reset();
        System.out.println(buf.position());//2

        //hasRemaining() : 判断缓冲区中是否有剩余的数据
        if(buf.hasRemaining()){
            //获取缓冲区中的可以操作的数量
            System.out.println(buf.remaining());//4
        }
    }

    @Test
    public void test1(){
        String str = "abcdef";
        //1.分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("-------------allocate()-----------");
        System.out.println(buf.position());//0
        System.out.println(buf.limit());//1024
        System.out.println(buf.capacity());//1024

        //2.利用put()存入数据到缓冲区中去
        buf.put(str.getBytes());
        System.out.println("-------------put()-----------");
        System.out.println(buf.position());//6
        System.out.println(buf.limit());//1024
        System.out.println(buf.capacity());//1024

        //3.切换到读取数据的模式
        buf.flip();
        System.out.println("-------------flip()-----------");
        System.out.println(buf.position());//0
        System.out.println(buf.limit());//6
        System.out.println(buf.capacity());//1024

        //4.利用get()读取缓冲区的数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println(new String(dst,0,dst.length));
        System.out.println("-------------put()-----------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //r.rewind :可重复读
        buf.rewind();
        System.out.println(new String(dst,0,dst.length));
        System.out.println("-------------rewind()-----------");
        System.out.println(buf.position());//0
        System.out.println(buf.limit());//6
        System.out.println(buf.capacity());//1024

        //6.clear() : 清空缓冲区,但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        buf.clear();
        System.out.println(new String(dst,0,dst.length));
        System.out.println("-------------clear()-----------");
        System.out.println(buf.position());//0
        System.out.println(buf.limit());//1024
        System.out.println(buf.capacity());//1024

        System.out.println((char)buf.get());//a
    }
}
