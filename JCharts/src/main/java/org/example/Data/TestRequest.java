package org.example.Data;

import lombok.Data;
import java.util.List;

@Data
public class TestRequest {

    /**
     * 协议类型
     * HTTP / TCP / WEBSOCKET / MYSQL / REDIS
     */
    private ProtocolType protocol;

    // ========================
    // ✅ HTTP 相关字段
    // ========================
    private String method;      // GET POST PUT DELETE
    private String url;
    private List<Header> headers;
    private String body;

    // ========================
    // ✅ TCP / Redis 相关
    // ========================
    private String host;
    private Integer port;
    private String message;     // TCP 发送内容

    // ========================
    // ✅ MySQL 相关
    // ========================
    private String jdbcUrl;
    private String username;
    private String password;
    private String sql;

    // ========================
    // ✅ 压测公共参数
    // ========================
    private int threads;
    private int qps;
    private int duration;

    @Data
    public static class Header {
        private String key;
        private String value;
    }
}