package org.example.Factory;

import org.example.Data.TestRequest;
import org.example.Sample.Sampler;
import org.example.Samples.*;
import org.example.Unitl.DataSourceBuilderUtil;

public class SamplerFactory {

    public static Sampler createSampler(TestRequest request) {

        switch (request.getProtocol()) {

            case HTTP:
                return new HttpSampler(
                        request.getUrl(),
                        request.getMethod(),
                        request.getHeaders(),
                        request.getBody()
                );

            case TCP:
                return new TcpSampler(
                        request.getHost(),
                        request.getPort(),
                        request.getMessage()
                );

            case WEBSOCKET:
                return new WebSocketSampler(request.getUrl());

            case MYSQL:
                return new MysqlSampler(
                        DataSourceBuilderUtil.buildDataSource(request),
                        request.getSql()
                );

            case REDIS:
                return new RedisSampler(
                        request.getHost(),
                        request.getPort()
                );

            default:
                throw new RuntimeException("不支持的协议");
        }
    }
}