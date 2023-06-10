package com.blockchainanalysisplatform.Data;


import lombok.Data;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Data

public class ClickhouseTransaction {
    private final String id,
            type,
            hash,
            nonce,
            blockNumber,
            transactionIndex,
            from,
            to,
            nodeName,
            input,
            status,
            r,
            s;

    private final Long gasLimit,v; //it's NON-HEX VALUE, it's DECIMAL
    private final Double value,gasPrice;
    private final LocalDateTime timestamp;

    public ClickhouseTransaction(ResultSet rs) throws SQLException {

        this.id = ((String) rs.getObject(1));
        this.type = ((String) rs.getObject(2));
        this.hash = ((String) rs.getObject(3));
        this.nonce = ((String) rs.getObject(4));
        this.blockNumber = ((String) rs.getObject(5));
        this.transactionIndex = ((String) rs.getObject(6));
        this.from = ((String) rs.getObject(7));
        this.to = ((String) rs.getObject(8));
        this.value = ((Double) rs.getObject(9));
        this.nodeName = ((String) rs.getObject(10));
        this.input = ((String) rs.getObject(11));
        this.timestamp = ((LocalDateTime) rs.getObject(12));
        this.status = ((String) rs.getObject(13));
        this.gasLimit = ((Long) rs.getObject(14));
        this.gasPrice = ((Double) rs.getObject(15));
        this.r = ((String) rs.getObject(16));
        this.s = ((String) rs.getObject(17));
        this.v = ((Long) rs.getObject(18));

    }

    public static ArrayList<String> getField(){
        return new ArrayList<>(Arrays.asList(
                "timestamp",
                "id",
                "type",
                "hash",
                "nonce",
                "blockNumber",
                "transactionIndex",
                "from",
                "to",
                "nodeName",
                "input",
                "status",
                "r",
                "s",
                "gasLimit",
                "v",
                "value",
                "gasPrice"
                ));
    }


}
