package com.ycnote.scan.pojo.socket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 游超
 * websocket前台命令
 */
@Getter
@Setter
@Slf4j
public class SocketCommand {

    public static String SEPARATOR = "\n";
    /**
     * 命令头
     */
    private  String head;

    /**
     * 次数
     */
    private int count;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 字节
     */
    private int byteInt;

    /**
     * 节点列表
     */
    List<String> nodeList;
}
