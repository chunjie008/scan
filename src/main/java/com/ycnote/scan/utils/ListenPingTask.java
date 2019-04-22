package com.ycnote.scan.utils;

import com.google.gson.GsonBuilder;
import com.ycnote.scan.constant.SocketResultConstant;
import com.ycnote.scan.pojo.ping.PingNodeResult;
import com.ycnote.scan.pojo.socket.PingSocketResult;
import com.ycnote.scan.socket.PingSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author 游超
 * 监听所有任务是否结束
 */
@Slf4j
public class ListenPingTask implements Runnable{

    /**
     * 所有命令已经结束
     */
    public static String ALL_END = "ALL_END";

    /**
     * 监听门栓
     */
    public  CountDownLatch latch ;

    /**
     * 用于回应websocket消息
     */
    public  PingSocket pingSocket;

    /**
     * 结果集
     */
    List<PingNodeResult> pingNodeResultList;

    public ListenPingTask(CountDownLatch latch, PingSocket pingSocket,List<PingNodeResult> pingNodeResultList) {
        this.latch = latch;
        this.pingSocket = pingSocket;
        this.pingNodeResultList=pingNodeResultList;
    }

    /**
     * 监听所有任务是否结束，所有任务结束后向前端发送消息
     */
    @Override
    public void run() {
        try{
            latch.await();
            PingSocketResult result = new PingSocketResult(SocketResultConstant.SOCKET,getBestNode(),ALL_END);
            pingSocket.sendMessage(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(result));
        }catch (InterruptedException e){
            log.debug("InterruptedException");
        }catch (Exception e){
            log.error("Listen is error",e);
        }
    }

    public PingNodeResult getBestNode(){
        List<PingNodeResult> minLostList = new LinkedList<PingNodeResult>();
        for(PingNodeResult node:pingNodeResultList){
            Integer minLost = Integer.valueOf(node.getEnding().getLostCount()) ;
            for(int i=0;i<=100;i++){
                minLost = i;
                return node;
            }
        }
        return minLostList.get(0);
    }
}
