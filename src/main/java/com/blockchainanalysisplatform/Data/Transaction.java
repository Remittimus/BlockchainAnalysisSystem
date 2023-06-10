package com.blockchainanalysisplatform.Data;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {

    //TODO fix parameters
    private final String time;
    private  final String block_id,index,hash,failed,type,sender,recipient,call_count,value,value_usd,internal_value,internal_value_usd,fee,fee_usd,gas_used,gas_limit,gas_price,input_hex,nonce,v,r,s;



}
