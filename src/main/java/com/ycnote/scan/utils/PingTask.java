package com.ycnote.scan.utils;


import com.ycnote.scan.pojo.ping.PingNodeResult;
import com.ycnote.scan.socket.PingSocket;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author 游超
 * ping任务
 */
public class PingTask implements Runnable {
    /**
     * ip地址
     */
    private String scanNode;
    /**
     * 次数
     */
    private int count;
    /**
     * 超时时间
     */
    private int timout;
    /**
     * socket对象用于发送消息
     */
    private PingSocket pingSocket;
    /**
     * 结果集
     */
    List<PingNodeResult> pingNodeResultList;
    /**
     * 监听门栓
     */
    public CountDownLatch latch ;

    /**
     * 字节
     */
    private int byteInt;

    public PingTask(String ip, int count, int timout,int byteInt, PingSocket pingSocket, CountDownLatch latch,List<PingNodeResult> pingNodeResultList) {
        this.scanNode = ip;
        this.count = count;
        this.timout = timout;
        this.pingSocket = pingSocket;
        this.latch =latch;
        this.byteInt = byteInt;
        this.pingNodeResultList = pingNodeResultList;
    }

    /**
     * 执行ping任务
     */
    @Override
    public void run() {
        Ping ping = new Ping(scanNode,count,timout,byteInt,pingNodeResultList);
        ping.ping(pingSocket,latch);
    }
}
