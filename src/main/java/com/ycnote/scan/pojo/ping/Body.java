package com.ycnote.scan.pojo.ping;

import com.google.gson.annotations.Expose;
import com.ycnote.scan.constant.Constant;
import lombok.Getter;
import lombok.Setter;


/**
 * @author 游超
 * ping的响应体
 */
@Setter
@Getter
public class Body {

    /**
     * 字节数
     */
    @Expose
    public String bytes;
    /**
     * 响应时间
     */
    @Expose
    public String time;
    /**
     * IP包被路由器丢弃之前允许通过的最大网段数量
     */
    @Expose
    public String timeToLive;
    /**
     * 默认字节数
     */
    public static final String DEFAULT_BYTES ="0";
    /**
     * 默认延迟时间
     */
    public static final String DEFAULT_TIME ="0ms";
    /**
     * 默认TTL
     */
    public static final String DEFAULT_TIMETOLIVE ="0";
    /**
     * 次数
     */
    public int index;

    public Body(String bytes, String time, String timeToLive, int index) {
        this.bytes = bytes;
        this.time = time;
        this.timeToLive = timeToLive;
        this.index = index;
    }

    /**
     * 解析字符串获取body
     * @param msg
     * @param i
     * @return
     */
    public static Body Init(String msg, int i) {
        Body body= null;
        switch (PingNodeResult.language){
            case CHINESE:
                if(msg.startsWith(Constant.ChinesePing.REQUEST_HEAD)){
                    body = new Body(DEFAULT_BYTES,DEFAULT_TIME,DEFAULT_TIMETOLIVE,i);
                    return body;
                }
                String[] chinese_arr = msg.split("字节=");
                String[] chinese_arr0 = chinese_arr[1].split(" ");
                body = new Body(chinese_arr0[0],chinese_arr0[1].replace("时间=","").replace("时间<",""),chinese_arr0[2].replace("TTL=",""),i);
                break;
            case ENGLISH:
                if(msg.startsWith(Constant.EnglishPing.REQUEST_HEAD)){
                    body = new Body(DEFAULT_BYTES,DEFAULT_TIME,DEFAULT_TIMETOLIVE,i);
                    return body;
                }
                String[] arr = msg.split("bytes=");
                String[] arr0 = arr[1].split(" ");
                body = new Body(arr0[0],arr0[1].replace("time=","").replace("time<",""),arr0[2].replace("TTL=",""),i);
                break;
        }
        return body;
    }

}
