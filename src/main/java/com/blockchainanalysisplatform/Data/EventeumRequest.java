package com.blockchainanalysisplatform.Data;



import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventeumRequest {


    private String type;
    private String transactionIdentifierValue;
    private String nodeName;
    private List<SubscriptionStatuses> statuses;

    public EventeumRequest(Subscription subscription){
        this.type = subscription.getType();
        this.transactionIdentifierValue = subscription.getAddress();
        this.nodeName = "default";
        this.statuses = new ArrayList<>(subscription.getStatuses());


    }
    public EventeumRequest( ){this.nodeName = "default";}

}
