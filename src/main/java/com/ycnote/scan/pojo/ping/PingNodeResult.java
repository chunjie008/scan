package com.ycnote.scan.pojo.ping;

import com.google.gson.annotations.Expose;
import com.ycnote.scan.constant.Constant;
import com.ycnote.scan.constant.Language;
import lombok.Data;

import java.util.LinkedList;

/**
 * @author  游超
 * ping的返回结果
 */
@Data
public class PingNodeResult {

    /**
     * 语言
     */
    public static Language language;

    /**
     * id
     */
    @Expose
    public String id;

    /**
     * ip地址
     */
    public String ipAddress;
    /**
     * 头信息
     */
    public String head;
    /**
     * 历次ping结果
     */
    public LinkedList<Body>  bodys = new LinkedList<Body>();
    /**
     * 实时body
     */
    @Expose()
    public String currentBody;
    /**
     * 最终结果
     */
    @Expose
    public Ending ending;
    /**
     * 结果字符串
     */

    /**
     * 解析信息
     * @param msg
     */
    public void analysisMessage(String msg){
        switch (language){
            case CHINESE:
                analysisChineseMessage(msg.replace("(已发送 1024) ",""));
                break;
            case ENGLISH:
                analysisEnglishMessage(msg);
                break;
        }
    }

    /**
     * 解析英文信息
     * @param msg
     */
    public void analysisEnglishMessage(String msg){
        if(msg.startsWith(Constant.EnglishPing.REPLY_HEAD)||msg.startsWith(Constant.EnglishPing.REQUEST_HEAD)){
            Body body = Body.Init(msg,bodys.size()+1);
            bodys.add(body);
            if(!Body.DEFAULT_TIMETOLIVE.equals(body.getTimeToLive())){
                setCurrentBody("第"+body.getIndex()+"次：bytes="+body.getBytes()+" time="+body.getTime()+" TTL="+body.getTimeToLive());
            }else{
                setCurrentBody("第"+body.getIndex()+"次：请求超时。");
            }
            currentEnding();
        }
        if(msg.startsWith(Constant.EnglishPing.PACKETS_HEAD)){
            ending.setPackage(msg);
        }
        if(msg.startsWith(Constant.EnglishPing.MINIMUM_HEAD)){
            ending.setTimes(msg);
            setEnding(ending);
        }
    }

    /**
     * 解析中文信息
     * @param msg
     */
    public void analysisChineseMessage(String msg){
        if(msg.startsWith(Constant.ChinesePing.REPLY_HEAD)||msg.startsWith(Constant.ChinesePing.REQUEST_HEAD)){
            Body body = Body.Init(msg,bodys.size()+1);
            bodys.add(body);
            if(!Body.DEFAULT_TIMETOLIVE.equals(body.getTimeToLive())){
                setCurrentBody("第"+body.getIndex()+"次：字节="+body.getBytes()+" 时间="+body.getTime()+" TTL="+body.getTimeToLive());
            }else{
                setCurrentBody("第"+body.getIndex()+"次：请求超时。");
            }
            currentEnding();
        }
        if(msg.startsWith(Constant.ChinesePing.PACKETS_HEAD)){
            ending.setPackage(msg);
        }
        if(msg.startsWith(Constant.ChinesePing.MINIMUM_HEAD)){
            ending.setTimes(msg);
            setEnding(ending);
        }
    }

    /**
     * 实时计算统计信息
     */
    public void currentEnding(){
        int received = 0;
        int allTime = 0;
        int minTime = Integer.MAX_VALUE;
        int maxTime = Integer.MIN_VALUE;
        int lost = 0;
        for(Body body:bodys){
            if(Body.DEFAULT_TIME.equals(body.getTime())){
                lost++;
                continue;
            }
            received++;
            int time = Integer.valueOf(body.getTime().replace("ms",""));
            allTime = allTime+time;
            if(time>maxTime){
                maxTime = time;
            }
            if(time<minTime){
                minTime = time;
            }
        }
        int lostCont = bodys.size() - received;
        int lostInt = (int) (((float)lostCont/(float)bodys.size())*100);

        String lostString = String.valueOf(lostInt)+"%";
        Ending ending = new Ending(String.valueOf(bodys.size()),String.valueOf(received),lostString);
        ending.setMinimum(minTime+"ms");
        ending.setMaximum(maxTime+"ms");
        ending.setLostCount(String.valueOf(lost));
        if(received!=0){
            ending.setAverage(allTime/received +"ms");
        }else{
            ending.setMinimum("- -");
            ending.setMaximum("- -");
            ending.setAverage("- -");
        }
        setEnding(ending);
    }

}
