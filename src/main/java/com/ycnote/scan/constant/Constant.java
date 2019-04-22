package com.ycnote.scan.constant;

import java.util.List;

/**
 * @author 游超
 * 常量表
 */
public class Constant {

    /**
     * 扫描节点里列表
     */
    public static List<String> nodeList;

    /**
     * 启动ping
     */
    public static final String PING_START = "PING_START";

    /**
     * 结束ping
     */
    public static final String PING_END = "PING_END";
    /**
     * ip 分隔符
     */
    public static final String IP_SEPARATOR = ",";

    /**
     * 线程超载
     */
    public static final String OVERLOAD = "OVERLOAD";

    /**
     * 编码格式
     */
    public static final String ENCODING = "UTF-8";

    /**
     * 成功的返回值
     */
    public static final String SUCCESS_RESULT = "0";

    /**
     * 英文ping命令
     */
    public class EnglishPing{
        /**
         * ping命令返回的第一行头
         */
        public static final String PINGING_HEAD = "Pinging";
        /**
         * ping命令持续返回头
         */
        public static final String REPLY_HEAD = "Reply";
        /**
         * ping超时返回头
         */
        public static final String REQUEST_HEAD = "Request";
        /**
         * ping统计返回第一行头
         */
        public static final String PING_HEAD = "Ping";
        /**
         * ping统计包信息头
         */
        public static final String PACKETS_HEAD = "Packets";
        /**
         * ping命令时间信息说明头
         */
        public static final String APPROXIMATE_HEAD = "Approximate";
        /**
         * ping命令时间信息头
         */
        public static final String MINIMUM_HEAD = "Minimum";
    }

    /**
     * 中文ping命令
     */
    public class ChinesePing{
        /**
         * ping命令返回的第一行头
         */
        public static final String PINGING_HEAD = "正在";
        /**
         * ping命令持续返回头
         */
        public static final String REPLY_HEAD = "来自";
        /**
         * ping超时返回头
         */
        public static final String REQUEST_HEAD = "请求超时";
        /**
         * ping统计返回第一行头
         */
        public static final String PING_HEAD = "Ping";
        /**
         * ping统计包信息头
         */
        public static final String PACKETS_HEAD = "数据包";
        /**
         * ping命令时间信息说明头
         */
        public static final String APPROXIMATE_HEAD = "往返行程的估计时间";
        /**
         * ping命令时间信息头
         */
        public static final String MINIMUM_HEAD = "最短";
    }
}
