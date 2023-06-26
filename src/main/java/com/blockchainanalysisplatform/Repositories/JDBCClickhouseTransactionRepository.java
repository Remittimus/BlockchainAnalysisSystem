package com.blockchainanalysisplatform.Repositories;

import com.blockchainanalysisplatform.Data.*;
import com.blockchainanalysisplatform.Services.ClickhouseStatementGeneratorService;
import com.clickhouse.jdbc.ClickHouseDataSource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCClickhouseTransactionRepository implements ClickhouseTransactionRepository {

    private final ClickHouseDataSource clickHouseDataSource;
    private final ClickhouseStatementGeneratorService generatorService;
    private final String dbName;


    private final String kafkaAddresses;

    @Autowired
    public JDBCClickhouseTransactionRepository(
                                               ClickHouseDataSource clickHouseDataSource,
                                               ClickhouseStatementGeneratorService generatorService,
                                               @Value("${kafkaAddresses}") String kafkaAddresses) {
        this.clickHouseDataSource = clickHouseDataSource;

        this.generatorService = generatorService;
        this.dbName ="Analys";
        //this.dbName="Tests";
        this.kafkaAddresses = kafkaAddresses;
    }
    public String getDbName(){
        return dbName;
    }


    @Override
    @SneakyThrows
    public ChartData findDataForChartsById(String subscriptionId) {


        ChartData data = new ChartData();
        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {

            ResultSet results = s.executeQuery(
                    "SELECT timestamp, value, gasPrice FROM " + dbName + ".sum_" + subscriptionId +
                            " order by timestamp");
            while (results.next()) {
                data.getDates().add((LocalDateTime) results.getObject(1));
                data.getValues().add((Double) results.getObject(2));
                data.getGasPrices().add((Double) results.getObject(3));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return data;
    }


    @SneakyThrows
    public Iterable<ClickhouseTransaction> findByIdWhereFilter(String subscriptionId,OnlineFilter filter){
        List<ClickhouseTransaction> results = new ArrayList<>();

        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {

            ResultSet rs = s.executeQuery(
                    generatorService.generateSelectStatementWithFilterById(subscriptionId, filter, dbName)
            );
            while (rs.next()) {
                results.add(new ClickhouseTransaction(rs));
            }
        }
        return results;
    }


    @Override
    @SneakyThrows
    public Iterable<ClickhouseTransaction> findAllById(String subscriptionId) {
        List<ClickhouseTransaction> results = new ArrayList<>();

        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {

            ResultSet rs = s.executeQuery(
                    "SELECT * FROM " + dbName + ".id_" + subscriptionId
            );
            while (rs.next()) {
                results.add(new ClickhouseTransaction(rs)
                );
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return results;
    }

    @SneakyThrows
    public void deleteKafkaMaterialViewById(String id){

        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
            s.execute(
                    "drop table IF EXISTS " + dbName + ".kafka_" + id + "_mv;"
            );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    public void deleteTableById(String id){


        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
        s.execute(
                "drop table IF EXISTS "+dbName+".id_"+id+";"
        );} catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    @SneakyThrows
    public void deleteKafkaById(String topicId){
        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
            s.execute(
                    "drop table IF EXISTS " + dbName + ".kafka_" + topicId + ";"
            );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }



    @Override
    @SneakyThrows
    public void createTablesAfterSubscription( Subscription subscription, Filter filter) {

        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
        //try(Statement s = DBConnection.createStatement()) {
            s.execute("CREATE DATABASE IF NOT EXISTS " + dbName + ";");
            s.execute("CREATE TABLE IF NOT EXISTS " + dbName + ".kafka_" + subscription.getTopicId() + "(\n" +
                    "    id      String,\n" +
                    "    type    String,\n" +
                    "    details String\n" +
                    ") ENGINE = Kafka\n" +
                    "      SETTINGS kafka_broker_list = '" + kafkaAddresses + "',\n" +
                    "          kafka_topic_list = 'transaction-events-" + subscription.getTopicId() + "',\n" +
                    "          kafka_group_name = 'GroupAnalys',\n" +
                    //                    "          kafka_group_name = '"+user.getId()+ "',\n" +
                    "          kafka_format = 'JSONEachRow',\n" +
                    "          kafka_row_delimiter = '\\n';");
            s.execute(
                    "CREATE TABLE IF NOT EXISTS " + dbName + ".id_" + subscription.getId() + "\n" +
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
                            "      ORDER BY id;"
            );
            s.execute(generatorService.generateStatementForMaterialViewWithFilter(subscription, filter, dbName));
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    @SneakyThrows
    public void createAnalysisTablesAfterSubscription( Subscription subscription){

        //TODO: Summing to Aggregating
        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
            s.execute(
                    "CREATE TABLE IF NOT EXISTS " + dbName + ".sum_" + subscription.getId() + "\n" +
                            "(\n" +
                            "    timestamp DateTime,\n" +
                            "    blockNumber String,\n" +
                            "    value Float64,\n" +
                            "    gasPrice Float64\n" +
                            ") ENGINE = SummingMergeTree()\n" +
                            "order by timestamp;"
            );

            s.execute(

                    "CREATE MATERIALIZED VIEW IF NOT EXISTS " + dbName + ".id_" + subscription.getId() + "_mv TO " + dbName + ".sum_" + subscription.getId() + " AS\n" +
                            "SELECT " +
                            "timestamp as timestamp,\n" +
                            "blockNumber as blockNumber,\n" +
                            "value as value,\n" +
                            "gasPrice as gasPrice\n" +
                            "FROM " + dbName + ".id_" + subscription.getId() + ";\n"

            );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void deleteSumTableById(String id){

        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
            s.execute(
                    "drop table IF EXISTS " + dbName + ".sum_" + id + ";"
            );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    };

    @SneakyThrows
    public void deleteTableMaterialViewById(String id){
        try(Statement s = clickHouseDataSource.getConnection().createStatement()) {
            s.execute(
                    "drop table IF EXISTS " + dbName + ".id_" + id + "_mv;"
            );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    };




}
