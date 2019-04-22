package com.ycnote.scan.socket;

import com.google.gson.Gson;
import com.ycnote.scan.constant.Constant;
import com.ycnote.scan.constant.SocketResultConstant;
import com.ycnote.scan.pojo.ping.PingNodeResult;
import com.ycnote.scan.pojo.socket.PingSocketResult;
import com.ycnote.scan.pojo.socket.ScanSocketResult;
import com.ycnote.scan.pojo.socket.SocketCommand;
import com.ycnote.scan.utils.DefaultThreadPoolExecutor;
import com.ycnote.scan.utils.ListenPingTask;
import com.ycnote.scan.utils.PingTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 游超
 * ping所用的websocket
 */
@Controller
@ServerEndpoint(value = "/ping/{sessionId}")
@Slf4j
public class PingSocket {

    /**
     * 用于存放所有连接对象暂时无用。
     */
    private static CopyOnWriteArraySet<PingSocket> webSocketSet = new CopyOnWriteArraySet<PingSocket>();
    /**
     * 会话对象，用于发送消息等
     */
    private Session session;
    /**
     * 会话id暂时无用
     */
    private String sessionId;
    /**
     * 默认线程池
     */
    private DefaultThreadPoolExecutor defaultThreadPoolExecutor ;

    /**
     * 接受消息锁
     */
    private static ReentrantLock onMessageLock = new ReentrantLock();
    /**
     * 发送消息锁
     */
    private static ReentrantLock sendLock = new ReentrantLock();
    /**
     * 建立连接锁
     */
    private static ReentrantLock openLock = new ReentrantLock();
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("sessionId") String sessionId, Session session) {
        openLock.lock();
        try{
            this.session = session;
            this.sessionId = sessionId;
            webSocketSet.add(this);
            for(PingSocket socket:webSocketSet){
                if(this.equals(socket)){
                }else {
                    socket.sendMessage(new ScanSocketResult(SocketResultConstant.OVERLENGTH, null, null).toString());
                    if(socket.defaultThreadPoolExecutor!=null){
                        socket.defaultThreadPoolExecutor.shutdownNow();
                    }
                }
            }
            ScanSocketResult result = new ScanSocketResult(SocketResultConstant.INIT_NODE_LIST,null,null);
            sendMessage(result.toString());
        }catch (Exception e){
            log.error("open WebSocket error",e);
        } finally {
            openLock.unlock();
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        if(defaultThreadPoolExecutor!=null){
            defaultThreadPoolExecutor.shutdownNow();
        }
    }

    /**
     * 接受消息
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        onMessageLock.lock();
        try {
            SocketCommand sc = new Gson().fromJson(message,SocketCommand.class);
            switch (sc.getHead()) {
                case "PING_START":
                    if(sc.getNodeList()!=null){
                        List<PingNodeResult> pingNodeResultList = new LinkedList<PingNodeResult>();
                        defaultThreadPoolExecutor = new DefaultThreadPoolExecutor();
                        CountDownLatch latch = new CountDownLatch(sc.getNodeList().size());
                        ListenPingTask listenPingTask = new ListenPingTask(latch,this,pingNodeResultList);
                        if(!defaultThreadPoolExecutor.executeTask(listenPingTask)){
                            rejected();
                            return ;
                        }
                        for(String node:sc.getNodeList()){
                            PingTask task = new PingTask(node, sc.getCount(),sc.getTimeout(),sc.getByteInt(),this,latch,pingNodeResultList);
                            if(!defaultThreadPoolExecutor.executeTask(task)){
                                rejected();
                                return ;
                            }
                        }
                    }
                    break;
                case "PING_END":
                    defaultThreadPoolExecutor.shutdownNow();
                    break;
            }
        } catch (Exception e) {
            log.error("onMessage error",e);
        } finally {
            onMessageLock.unlock();
        }
    }

    /**
     * 拒绝执行任务
     */
    public void rejected(){
        PingSocketResult result = new PingSocketResult(SocketResultConstant.SOCKET,null,Constant.OVERLOAD);
        sendMessage(result.toString());
    }

    /**
     * 发信息
     *
     * @param message
     */
    public void sendMessage(String message) {
        sendLock.lock();
        try {
            if (!Thread.currentThread().isInterrupted()) {
                this.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("send msg error",e);
        } finally {
            sendLock.unlock();
        }
    }

}
