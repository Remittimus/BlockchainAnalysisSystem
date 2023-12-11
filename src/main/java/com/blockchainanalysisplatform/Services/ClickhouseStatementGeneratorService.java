package com.blockchainanalysisplatform.Services;

import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.FilterInterface;
import com.blockchainanalysisplatform.Data.OnlineFilter;
import com.blockchainanalysisplatform.Data.Subscription;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

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
            addWheretoStatement(result,filter);
            if(!filter.getToAddress().isEmpty()) result.append("(to == '").append(filter.getToAddress()).append("') AND");
            if(!filter.getFromAddress().isEmpty()) result.append("(from == '").append(filter.getFromAddress()).append("') AND");

            int lastIndex = result.lastIndexOf(" AND");
            if( lastIndex == result.length()-4) {
                result.delete(lastIndex,result.length());
            }
            result.append(";");
        }

        return result.toString();
    }


    public String generateSelectStatementWithFilterById(String id, OnlineFilter filter, String dbName) {
        StringBuilder result = new StringBuilder(
                "SELECT * FROM " + dbName + ".id_" + id + "\n"
        );

        if (!filter.isEmpty()) {
            addWheretoStatement(result,filter);
        }
        result.append("\n");
        result.append("ORDER BY ").append(filter.getOrderBy()).append(" ").append(filter.getOrderType());
        result.append(";");

        return result.toString();
    }

    private void addWheretoStatement(StringBuilder result, FilterInterface filter){ //TODO mb map without fields
        result.append("WHERE ");
        if (filter.getMaxValue() != null) result.append("(value < ").append(filter.getMaxValue()).append(") AND");
        if (filter.getMinValue() != null) result.append("(value > ").append(filter.getMinValue()).append(") AND");
        if (filter.getStartDate() != null)
            result.append("(timestamp >= '").append(filter.getStartDate().toEpochSecond(ZoneOffset.UTC)).append("') AND");
        if (filter.getEndDate() != null)
            result.append("(timestamp <= '").append(filter.getEndDate().toEpochSecond(ZoneOffset.UTC)).append("') AND");
        result.delete(result.lastIndexOf(" AND"), result.length());


    }
}
