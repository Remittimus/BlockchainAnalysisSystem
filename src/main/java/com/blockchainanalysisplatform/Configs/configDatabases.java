package com.blockchainanalysisplatform.Configs;


import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
public class configDatabases {


    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}")String username,
            @Value("${spring.datasource.password}")String password,
            @Value("${spring.datasource.driverClassName}")String driverName
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(driverName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);

        return dataSource;
    }


    @Bean
    ClickHouseDataSource clickhouseDataSource(@Value("${clickhouse.url}") String url,
                                              @Value("${clickhouse.username}")String username,
                                              @Value("${clickhouse.password}")String password) throws SQLException {


        Properties pr = new Properties();
        pr.setProperty("user", username);
        pr.setProperty("password", password);
        return new ClickHouseDataSource(url, pr);

    }


}
