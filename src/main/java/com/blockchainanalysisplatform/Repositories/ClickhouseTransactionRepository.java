package com.blockchainanalysisplatform.Repositories;

import com.blockchainanalysisplatform.Data.*;



public interface ClickhouseTransactionRepository  {




    Iterable<ClickhouseTransaction> findAllById(String subscriptionId);

    Iterable<ClickhouseTransaction> findByIdWhereFilter(String subscriptionId,OnlineFilter filter);

    ChartData findDataForChartsById(String subscriptionId); //TODO: mb to iterable

    void createTablesAfterSubscription( Subscription subscription, Filter filter);

    void createAnalysisTablesAfterSubscription( Subscription subscription);

    void deleteSumTableById(String id);

    void deleteTableMaterialViewById(String id);
    void deleteTableById(String id);

    void deleteKafkaMaterialViewById(String id);

    void deleteKafkaById(String id);

}
