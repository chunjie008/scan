package com.ycnote.scan.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 游超
 * webscoket配置
 */
@Configuration
public class WebSocketConfig {
    /**
     * 注入ServerEndpointExporter 不使用独立的servlet容器
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
