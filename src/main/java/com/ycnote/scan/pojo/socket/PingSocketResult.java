package com.ycnote.scan.pojo.socket;
import com.google.gson.annotations.Expose;
import com.ycnote.scan.pojo.ping.PingNodeResult;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 游超
 * ping返回结果 用于响应socket
 */
@Setter
@Getter
public class PingSocketResult {


    /**分割符**/
    public static String SEPARATOR = "\n";

    /**
     * 消息类型
     */
    @Expose
    private String type;

    /**
     * ip地址
     */
    @Expose
    private PingNodeResult pingNodeResult ;

    /**
     * 信息
     */
    @Expose
    private String msg;

    public PingSocketResult(String type, PingNodeResult pingNodeResult, String msg) {
        this.type = type;
        this.pingNodeResult = pingNodeResult;
        this.msg = msg;
    }

}
