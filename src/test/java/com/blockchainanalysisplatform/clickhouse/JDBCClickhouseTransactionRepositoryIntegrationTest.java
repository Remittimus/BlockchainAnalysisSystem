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
import org.springframework.test.context.TestPropertySource;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@ContextConfiguration(classes = {configDatabases.class,JDBCClickhouseTransactionRepository.class, ClickhouseStatementGeneratorService.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {"clickhouse.dbName=Tests"})
class JDBCClickhouseTransactionRepositoryIntegrationTest {

    @Autowired
    private JDBCClickhouseTransactionRepository repository;

    @Autowired
    private ClickHouseDataSource clickHouseDataSource;


    @BeforeAll
    public void setUp() {
        assertEquals("Tests", repository.getDbName(),"Clickhouse must be set up for testing. The \"Tests\" database is required.");
    }



    @Test
    void testCreateAndDeleteTables_ShouldCreateAndDeleteTablesForSubscription() {

        Subscription subscription = new Subscription();
        subscription.setId("12345");
        subscription.setTopicId("12345");
        Filter filter = new Filter();


        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {

            repository.createTablesAfterSubscription(subscription, filter);
            repository.createAnalysisTablesAfterSubscription(subscription);
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
