package com.blockchainanalysisplatform.clickhouse;

import com.blockchainanalysisplatform.Configs.configDatabases;
import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Repositories.JDBCClickhouseTransactionRepository;
import com.blockchainanalysisplatform.Services.ClickhouseStatementGeneratorService;
import com.clickhouse.jdbc.ClickHouseDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;


@Disabled
@SpringBootTest
@ContextConfiguration(classes = {configDatabases.class,JDBCClickhouseTransactionRepository.class, ClickhouseStatementGeneratorService.class})
class JDBCClickhouseTransactionRepositoryIntegrationTest {

    @Autowired
    private JDBCClickhouseTransactionRepository repository;

    @Autowired
    private ClickHouseDataSource clickHouseDataSource;
    //private Connection connection;



    @Test
    void testCreateAndDeleteTables_ShouldCreateAndDeleteTablesForSubscription() {
        // Arrange
        if (!repository.getDbName().equals("Tests") ) {
            throw new RuntimeException("Error, clickhouse is not configure for integration tests");
        }
        Subscription subscription = new Subscription();
        subscription.setId("12345");
        subscription.setTopicId("12345");
        Filter filter = new Filter();

        // Act
        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
        //try {
            repository.createTablesAfterSubscription(subscription, filter);
            repository.createAnalysisTablesAfterSubscription(subscription);
            //Statement s = connection.createStatement();
            int count = 0;
            ResultSet rs = s.executeQuery(
                    "SELECT database, name FROM system.tables\n" +
                            "WHERE (database='Tests') AND (\n" +
                            "    (name='id_"+subscription.getId()+"') OR (name='sum_"+subscription.getId()+"') OR (name='kafka_"+subscription.getTopicId()+"') OR (name='id_"+subscription.getId()+"_mv') OR (name='kafka_"+subscription.getId()+"_mv')\n" +
                            "    );"
            );
            while(rs.next()){
                count++;
            }
            assertEquals(5, count);
            repository.deleteTableById(subscription.getId());
            repository.deleteSumTableById(subscription.getId());
            repository.deleteTableMaterialViewById(subscription.getId());
            repository.deleteKafkaMaterialViewById(subscription.getId());
            repository.deleteKafkaById(subscription.getTopicId());
             rs = s.executeQuery(
                    "SELECT database, name FROM system.tables\n" +
                            "WHERE (database='Tests') AND (\n" +
                            "    (name='id_"+subscription.getId()+"') OR (name='sum_"+subscription.getId()+"') OR (name='kafka_"+subscription.getTopicId()+"') OR (name='id_"+subscription.getId()+"_mv') OR (name='kafka_"+subscription.getId()+"_mv')\n" +
                            "    );"
            );
             assertFalse(rs.next());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

}
