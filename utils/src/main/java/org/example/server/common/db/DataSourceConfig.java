package org.example.server.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {

    public static DataSource createDataSource(RdsParam rdsParam) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(rdsParam.getUrl());
        config.setUsername(rdsParam.getUsername());
        config.setPassword(rdsParam.getPassword());

        // 设置连接池的参数
        config.setMaximumPoolSize(100);  // 根据需要调整
        config.setMinimumIdle(20);       // 根据需要调整
        config.setIdleTimeout(60000);    // 60秒
        config.setConnectionTimeout(30000); // 30秒
        config.setMaxLifetime(1800000);  // 30分钟

        // 设置连接测试查询
        config.setConnectionTestQuery("SELECT 1");

        // 其他优化参数
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }
}
