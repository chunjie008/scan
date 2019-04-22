package com.ycnote.scan.pojo.ping;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 游超
 * ping的最终结果
 */
@Setter
@Getter
public class Ending {
    /**
     * 发送次数
     */
    @Expose
    public String sent ;
    /**
     * 成功次数
     */
    @Expose
    public String received;
    /**
     * 丢包次数
     */
    @Expose
    public String lostCount;
    /**
     * 丢包率
     */
    @Expose
    public String lost;
    /**
     * 最小时间
     */
    @Expose
    public String minimum;
    /**
     * 最大时间
     */
    @Expose
    public String maximum;
    /**
     * 平均时间
     */
    @Expose
    public String average;

    public Ending(String sent, String received, String lost) {
        this.sent = sent;
        this.received = received;
        this.lost = lost;
    }

    /**
     * 分析统计时间
     * @param times
     */
    public void setTimes(String times) {
        String[] arr = times.split(" ");
        switch (PingNodeResult.language) {
            case CHINESE:
                setMinimum(arr[2].replace("，最长",""));
                setMaximum(arr[4].replace("，平均",""));
                setAverage(arr[6]);
                break;
            case ENGLISH:
                setMinimum(arr[2].substring(0,arr[2].length()-1));
                setMaximum(arr[5].substring(0,arr[5].length()-1));
                setAverage(arr[8]);
                break;
        }
    }

    /**
     * 分析统计包信息
     * @param msg
     */
    public void setPackage(String msg) {
        String[] arr = msg.split(" ");
        switch (PingNodeResult.language) {
            case CHINESE:
                setSent(arr[3].replace("，已接收",""));
                setReceived(arr[5].replace("，丢失",""));
                setLostCount(arr[7]);
                setLost(arr[8].replace("(",""));
                break;
            case ENGLISH:
                setSent(arr[3].substring(0,arr[3].length()-1));
                setReceived(arr[6].substring(0,arr[6].length()-1));
                setLostCount(arr[9]);
                setLost(arr[10].replace("(",""));
                break;
        }
    }
}
