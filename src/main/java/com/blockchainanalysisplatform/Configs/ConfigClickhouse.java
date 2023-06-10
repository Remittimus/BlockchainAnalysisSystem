package com.blockchainanalysisplatform.Configs;


import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class ConfigClickhouse {


    @Bean
    Connection clickhouseConnection(@Value("${clickhouse.url}") String url,
                                    @Value("${clickhouse.username}")String username,
                                    @Value("${clickhouse.password}")String password) throws SQLException {


        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, new Properties());
        return dataSource.getConnection(username, password);

    }
}
