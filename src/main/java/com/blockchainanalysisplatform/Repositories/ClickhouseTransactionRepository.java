package com.blockchainanalysisplatform.Repositories;

import com.blockchainanalysisplatform.Data.*;
import com.blockchainanalysisplatform.Exceptions.DataAccessException;



public interface ClickhouseTransactionRepository  {




    Iterable<ClickhouseTransaction> findAllById(String subscriptionId) throws DataAccessException;

    Iterable<ClickhouseTransaction> findByIdWhereFilter(String subscriptionId,OnlineFilter filter) throws DataAccessException;

    ChartData findDataForChartsById(String subscriptionId) throws DataAccessException;

    void createTablesAfterSubscription( Subscription subscription, Filter filter) throws DataAccessException;

    void createAnalysisTablesAfterSubscription( Subscription subscription) throws DataAccessException;

    void deleteSumTableById(String id) throws DataAccessException;

    void deleteTableMaterialViewById(String id) throws DataAccessException;
    void deleteTableById(String id) throws DataAccessException;

    void deleteKafkaMaterialViewById(String id) throws DataAccessException;

    void deleteKafkaById(String id) throws DataAccessException;

}
