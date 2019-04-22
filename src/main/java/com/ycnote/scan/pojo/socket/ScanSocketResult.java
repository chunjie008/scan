package com.ycnote.scan.pojo.socket;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 游超
 * 初始化扫描节点
 */
@Setter
@Getter
public class ScanSocketResult {

    /**
     * 消息类型
     */
    @Expose
    private String type;

    /**
     * ip地址
     */
    @Expose
    private List<String> data;

    /**
     * 信息
     */
    @Expose
    private String msg;

    public ScanSocketResult(String type, List<String> data, String msg) {
        this.type = type;
        this.data = data;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }
}
