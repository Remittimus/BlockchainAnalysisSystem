package com.blockchainanalysisplatform.Services;

import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.FilterInterface;
import com.blockchainanalysisplatform.Data.OnlineFilter;
import com.blockchainanalysisplatform.Data.Subscription;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;

@Service
public class ClickhouseStatementGeneratorService {

    public String generateStatementForMaterialViewWithFilter(Subscription subscription, Filter filter, String dbName){

        StringBuilder result = new StringBuilder(

        "CREATE MATERIALIZED VIEW IF NOT EXISTS " + dbName + ".kafka_" + subscription.getId() + "_mv TO " + dbName + ".id_" + subscription.getId() + " AS\n" +
                "SELECT * FROM\n"+
                "(SELECT id,\n" +
                "       type,\n" +
                "       JSONExtractString(details, 'hash')             as hash,\n" +
                "       JSONExtractString(details, 'nonce') as nonce,\n" +
                "       JSONExtractString(details, 'blockNumber') as blockNumber,\n" +
                "       JSONExtractString(details, 'transactionIndex') as transactionIndex,\n" +
                "       JSONExtractString(details, 'from')             as from,\n" +
                "       JSONExtractString(details, 'to')               as to,\n" +
                "       toFloat64(bitAnd(reinterpretAsInt64(reverse(unhex(substring(JSONExtractString(details, 'value'), 3)))),\n" +
                "                        toUInt64('9223372036854775807'))) / 1000000000000000000\n" + //TODO: find a prettier solution for converting a number of 18 digits
                "                                                           as value,\n" +
                "       JSONExtractString(details, 'nodeName')         as nodeName,\n" +
                "       JSONExtractString(details, 'input')            as input,\n" +
                "       toDateTime(JSONExtractString(details, 'timestamp'))        as timestamp,\n" +
                "       JSONExtractString(details, 'status')           as status,\n" +
                "       toInt64(bitAnd(reinterpretAsInt64(reverse(unhex(substring(\n" +
                "                     JSONExtractString(details, 'gas'), 3)))), toUInt64('9223372036854775807')))\n" +
                "                                                                 as gasLimit,\n" +
                "       toFloat64(bitAnd(reinterpretAsInt64(reverse(unhex(substring(\n" +
                "                     JSONExtractString(details, 'gasPrice'), 3)))), toUInt64('9223372036854775807'))) / 1000000000\n" +
                "                                                                 as gasPrice,\n"+
                "       JSONExtractString(details, 'r')                    as r,\n" +
                "       JSONExtractString(details, 's')                  as s,\n" +
                "       toInt64(JSONExtractString(details, 'v'))                    as v\n"+
                "FROM " + dbName + ".kafka_" + subscription.getTopicId()+")"
        );
        if(!filter.isEmpty()){
            result.append(getWhereStatement(filter));
            if(!filter.getToAddress().isEmpty()) result.append(" AND (to == '").append(filter.getToAddress()).append("')");
            if(!filter.getFromAddress().isEmpty()) result.append(" AND (from == '").append(filter.getFromAddress()).append("')");
            result.append(";");
        }

        return result.toString();
    }


    public String generateSelectStatementWithFilterById(String id, OnlineFilter filter, String dbName) {
        StringBuilder result = new StringBuilder(
                "SELECT * FROM " + dbName + ".id_" + id + "\n"
        );

        if (!filter.isEmpty()) {
            result.append(getWhereStatement(filter));
        }
        result.append("\n");
        result.append("ORDER BY ").append(filter.getOrderBy()).append(" ").append(filter.getOrderType());
        result.append(";");

        return result.toString();
    }

    private void addWheretoStatement(StringBuilder result, FilterInterface filter){
        result.append("WHERE ");
        if (filter.getMaxValue() != null) result.append("(value < ").append(filter.getMaxValue()).append(") AND");
        if (filter.getMinValue() != null) result.append("(value > ").append(filter.getMinValue()).append(") AND");
        if (filter.getStartDate() != null)
            result.append("(timestamp >= '").append(filter.getStartDate().toEpochSecond(ZoneOffset.UTC)).append("') AND");
        if (filter.getEndDate() != null)
            result.append("(timestamp <= '").append(filter.getEndDate().toEpochSecond(ZoneOffset.UTC)).append("') AND");
        result.delete(result.lastIndexOf(" AND"), result.length());


    }

    private String getWhereStatement(FilterInterface filter){
        Map<Supplier<Object>, String> conditions = new LinkedHashMap<>();
        conditions.put(filter::getMaxValue, "(value < %s)");
        conditions.put(filter::getMinValue, "(value > %s)");
        conditions.put(() -> {
            if (filter.getStartDate() != null) return filter.getStartDate().toEpochSecond(ZoneOffset.UTC);
            else return null;
        }, "(timestamp >= '%s')");
        conditions.put(() -> {
            if (filter.getEndDate() != null) return filter.getEndDate().toEpochSecond(ZoneOffset.UTC);
            else return null;
        }, "(timestamp <= '%s')");

        StringJoiner whereStatement = new StringJoiner(" AND ", "WHERE ", "");
        for (Map.Entry<Supplier<Object>, String> condition : conditions.entrySet()) {
            Object value = condition.getKey().get();
            if (value != null) {
                whereStatement.add(String.format(condition.getValue(), value.toString()));
            }
        }

        return (whereStatement.length() > 6) ? whereStatement.toString() : ""; // If no conditions added, return empty string
    }


}
