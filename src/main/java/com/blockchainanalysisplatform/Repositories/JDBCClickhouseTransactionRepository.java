package com.blockchainanalysisplatform.Repositories;

import com.blockchainanalysisplatform.Data.*;
import com.blockchainanalysisplatform.Exceptions.DataAccessException;
import com.blockchainanalysisplatform.Services.ClickhouseStatementGeneratorService;
import com.clickhouse.jdbc.ClickHouseConnection;
import com.clickhouse.jdbc.ClickHouseDataSource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCClickhouseTransactionRepository implements ClickhouseTransactionRepository {

    private final ClickHouseDataSource clickHouseDataSource;
    private final ClickhouseStatementGeneratorService generatorService;
    @Getter
    private final String dbName;


    private final String kafkaAddresses;

    @Autowired
    public JDBCClickhouseTransactionRepository(
            ClickHouseDataSource clickHouseDataSource,
            ClickhouseStatementGeneratorService generatorService,
            @Value("${kafkaAddresses}") String kafkaAddresses,
            @Value("${clickhouse.dbName}") String dbName) {
        this.clickHouseDataSource = clickHouseDataSource;

        this.generatorService = generatorService;
        this.dbName = dbName;
        //this.dbName="Tests";
        this.kafkaAddresses = kafkaAddresses;
    }


    public ChartData findDataForChartsById(String subscriptionId) throws DataAccessException {
        ChartData data = new ChartData();

        String query = "SELECT timestamp, value, gasPrice FROM " + dbName + ".sum_" + subscriptionId + " order by timestamp";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.getDates().add((LocalDateTime) rs.getObject(1));
                data.getValues().add((Double) rs.getObject(2));
                data.getGasPrices().add((Double) rs.getObject(3));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing data for charts", e);
        }
        return data;
    }



    public Iterable<ClickhouseTransaction> findByIdWhereFilter(String subscriptionId, OnlineFilter filter) throws DataAccessException{
        List<ClickhouseTransaction> results = new ArrayList<>();


        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(generatorService.generateSelectStatementWithFilterById(subscriptionId, filter, dbName));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new ClickhouseTransaction(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing data for transactions", e);
        }
        return results;
    }


    @Override
    public Iterable<ClickhouseTransaction> findAllById(String subscriptionId) throws DataAccessException{
        List<ClickhouseTransaction> results = new ArrayList<>();

        String query = "SELECT * FROM " + dbName + ".id_" + subscriptionId;
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new ClickhouseTransaction(rs)
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing data for transactions", e);
        }

        return results;
    }


    public void deleteKafkaMaterialViewById(String id) throws DataAccessException{

        String query = "drop table IF EXISTS " + dbName + ".kafka_" + id + "_mv;";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.executeQuery();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting Kafka's material view in clickhouse", e);
        }
    }


    public void deleteTableById(String id) throws DataAccessException{

        String query = "drop table IF EXISTS " + dbName + ".id_" + id + ";";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.executeQuery();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting transactions table in clickhouse", e);
        }

    }


    public void deleteKafkaById(String topicId) throws DataAccessException{

        String query = "drop table IF EXISTS " + dbName + ".kafka_" + topicId + ";";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.executeQuery();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting kafka table in clickhouse", e);
        }

    }


    @Override
    public void createTablesAfterSubscription(Subscription subscription, Filter filter) throws DataAccessException{

        String queryCreateDatabase = "CREATE DATABASE IF NOT EXISTS " + dbName + ";";
        String queryCreateKafkaTable = "CREATE TABLE IF NOT EXISTS " + dbName + ".kafka_" + subscription.getTopicId() + "(\n" +
                "    id      String,\n" +
                "    type    String,\n" +
                "    details String\n" +
                ") ENGINE = Kafka\n" +
                "      SETTINGS kafka_broker_list = '" + kafkaAddresses + "',\n" +
                "          kafka_topic_list = 'transaction-events-" + subscription.getTopicId() + "',\n" +
                "          kafka_group_name = 'GroupAnalys',\n" +
                //                    "          kafka_group_name = '"+user.getId()+ "',\n" +
                "          kafka_format = 'JSONEachRow',\n" +
                "          kafka_row_delimiter = '\\n';";
        String queryCreateSubscriptionsTable = "CREATE TABLE IF NOT EXISTS " + dbName + ".id_" + subscription.getId() + "\n" +
                "(\n" +
                "    id               String,\n" +
                "    type             String,\n" +
                "    hash             String,\n" +
                "    nonce            String,\n" +
                "    blockNumber      String,\n" +
                "    transactionIndex String,\n" +
                "    from             String,\n" +
                "    to               String,\n" +
                "    value            Float64,\n" +
                "    nodeName         String,\n" +
                "    input            String,\n" +
                "    timestamp        DateTime,\n" +
                "    status           String,\n" +
                "    gasLimit         Int64,\n" +
                "    gasPrice         Float64,\n" +
                "    r                String,\n" +
                "    s                String,\n" +
                "    v                Int64\n" +
                ") ENGINE = ReplacingMergeTree()\n" +
                "      ORDER BY id;";
        String queryCreateMaterialViews = generatorService.generateStatementForMaterialViewWithFilter(subscription, filter, dbName);

        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement psCreateDatabase = connection.prepareStatement(queryCreateDatabase);
            PreparedStatement psCreateKafkaTable = connection.prepareStatement(queryCreateKafkaTable);
            PreparedStatement psCreateSubscriptionsTable = connection.prepareStatement(queryCreateSubscriptionsTable);
            PreparedStatement psCreateMaterialViews = connection.prepareStatement(queryCreateMaterialViews);

            psCreateDatabase.executeQuery();
            psCreateKafkaTable.executeQuery();
            psCreateSubscriptionsTable.executeQuery();
            psCreateMaterialViews.executeQuery();

        } catch (SQLException e) {
            throw new DataAccessException("Error creating tables in clickhouse", e);
        }



    }


    public void createAnalysisTablesAfterSubscription(Subscription subscription) throws DataAccessException{


        String queryStatisticsTable = "CREATE TABLE IF NOT EXISTS " + dbName + ".sum_" + subscription.getId() + "\n" +
                "(\n" +
                "    timestamp DateTime,\n" +
                "    blockNumber String,\n" +
                "    value Float64,\n" +
                "    gasPrice Float64\n" +
                ") ENGINE = AggregatingMergeTree()\n" +
                "order by timestamp;";
        String queryCreateMaterialViewForStatisticsTable = "CREATE MATERIALIZED VIEW IF NOT EXISTS " + dbName + ".id_" + subscription.getId() + "_mv TO " + dbName + ".sum_" + subscription.getId() + " AS\n" +
                "SELECT " +
                "timestamp as timestamp,\n" +
                "blockNumber as blockNumber,\n" +
                "sum(value) as value,\n" +
                "avg(gasPrice) as gasPrice\n" +
                "FROM " + dbName + ".id_" + subscription.getId() + "\n" +
                "GROUP BY timestamp, blockNumber;";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement psStatisticsTable = connection.prepareStatement(queryStatisticsTable);
            PreparedStatement psCreateMaterialViewForStatisticsTable = connection.prepareStatement(queryCreateMaterialViewForStatisticsTable);


            psStatisticsTable.executeQuery();
            psCreateMaterialViewForStatisticsTable.executeQuery();


        } catch (SQLException e) {
            throw new DataAccessException("Error creating analysis tables in clickhouse", e);
        }

    }


    public void deleteSumTableById(String id) throws DataAccessException{
        String query = "drop table IF EXISTS " + dbName + ".sum_" + id + ";";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.executeQuery();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting sum table in clickhouse", e);
        }

    }




    public void deleteTableMaterialViewById(String id) throws DataAccessException{

        String query = "drop table IF EXISTS " + dbName + ".id_" + id + "_mv;";
        try (ClickHouseConnection connection = clickHouseDataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.executeQuery();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting sum table in clickhouse", e);
        }

    }


}
