package com.ycnote.scan.utils;

import com.google.gson.GsonBuilder;
import com.ycnote.scan.constant.Language;
import com.ycnote.scan.constant.SocketResultConstant;
import com.ycnote.scan.pojo.ping.PingNodeResult;
import com.ycnote.scan.pojo.socket.PingSocketResult;
import com.ycnote.scan.socket.PingSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author 游超
 * ping命令模拟
 */
@Slf4j
public class Ping {
    /**
     * 检测节点
     */
    private String ipAddress ;

    /**
     * 字节
     */
    private int byteInt;
    /**
     * 检测节点
     */
    private String ip ;
    /**
     * 默认ping次数
     */
    private int pingTimes = 5;
    /**
     * 默认超时时间5秒
     */
    private int timeOut = 5000;
    /**
     * 参数表，参数名为key，参数值为value
     */
    private HashMap params = new HashMap<String, Object>();
    /**
     * 结果对象
     */
    public PingNodeResult  pingNodeResult = new PingNodeResult();
    /**
     * 结果集
     */
    List<PingNodeResult> pingNodeResultList;

    /**
     * 构造方法
     *
     * @param ip
     * @param pingTimes
     * @param timeOut
     */
    public Ping(String ip, int pingTimes, int timeOut,int byteInt,List<PingNodeResult> pingNodeResultList) {
        this.pingTimes = pingTimes;
        this.timeOut = timeOut;
        this.byteInt = byteInt;
        this.ipAddress = ip;
        params.put(" -n ", pingTimes);
        params.put(" -w ", timeOut);
        params.put(" -l ", byteInt);
        this.pingNodeResult.setId(ip);
        this.pingNodeResultList = pingNodeResultList;
    }

    /**
     * 构造方法
     *
     * @param ip
     * @param pingTimes
     * @param timeOut
     */
    public Ping(String ip, int pingTimes, int timeOut) {
        this.pingTimes = pingTimes;
        this.timeOut = timeOut;
        this.ipAddress = ip;
        params.put(" -n ", pingTimes);
        params.put(" -w ", timeOut);
        this.pingNodeResult.setId(ip);
    }

    /**
     * 获取命令
     *
     * @return
     */
    public String getCommand() {
        StringBuilder command = new StringBuilder("ping "+ipAddress+" ");
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            command.append(entry.getKey());
            command.append(entry.getValue());
        }
        return command.toString();
    }


    /**
     * ping命令模拟
     *
     * @return
     */
    public void ping(PingSocket pingSocket, CountDownLatch latch) {
        BufferedReader in = null;
        Process p = null;
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = getCommand();
        try {   // 执行命令并获取输出
            p = r.exec(pingCommand);
            if (p == null) {
                return ;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), System.getProperty("sun.jnu.encoding")));   // 防止乱码获取系统编码
            String line = null;
            while ((line = in.readLine()) != null) {
                if(Thread.currentThread().isInterrupted()){
                    throw new InterruptedException();
                }
                if("".equals(line)){
                    continue;
                }
                line = line.trim();
                pingNodeResult.analysisMessage(line);
                if(pingNodeResult.bodys.size()>0){
                    PingSocketResult result = new PingSocketResult(SocketResultConstant.PING_PROGRESS,pingNodeResult,null);
                    String resultString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create().toJson(result);
                    pingSocket.sendMessage(resultString);
                }
            }
        }catch (InterruptedException e) {
            log.debug("InterruptedException");
        } catch (Exception ex) {
            log.error("cmd is error",ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if(p!=null){
                    p.destroy();
                }
                pingNodeResultList.add(pingNodeResult);
                latch.countDown();
            } catch (IOException e) {
                log.error("close io error",e);
            }
        }
    }

    /**
     * 通过ping设置语言
     */
    public void setLanguage() {
        BufferedReader in = null;
        Process p = null;
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = getCommand();
        try {   // 执行命令并获取输出
            p = r.exec(pingCommand);
            if (p == null) {
                return ;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), System.getProperty("sun.jnu.encoding")));   // 防止乱码获取系统编码
            String line = null;
            while ((line = in.readLine()) != null) {
                if("".equals(line)){
                    continue;
                }
                if(line.startsWith("Pinging")){
                    PingNodeResult.language = Language.ENGLISH;
                    break;
                }else{
                    PingNodeResult.language = Language.CHINESE;
                    break;
                }
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
        } catch (Exception ex) {
            log.error("cmd is error",ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if(p!=null){
                    p.destroy();
                }
            } catch (IOException e) {
                log.error("close io error",e);
            }
        }
    }

}
