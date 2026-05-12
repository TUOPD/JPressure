package org.example.Unitl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.Data.TestRequest;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceBuilderUtil {
    private static final Map<String, DataSource> dsCache = new ConcurrentHashMap<>();
    public static DataSource buildDataSource(TestRequest request) {

        return dsCache.computeIfAbsent(request.getJdbcUrl(), key -> {

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(request.getJdbcUrl());
            config.setUsername(request.getUsername());
            config.setPassword(request.getPassword());
            config.setMaximumPoolSize(20);

            return new HikariDataSource(config);
        });
    }
}
