package com.blockchainanalysisplatform.Services;

import com.blockchainanalysisplatform.Data.*;
import com.blockchainanalysisplatform.Exceptions.DataAccessException;
import com.blockchainanalysisplatform.Repositories.ClickhouseTransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class ClickhouseService {

    ClickhouseTransactionRepository clickhouseRepo;

    //TODO: rewrite this to optional, rewrite frontend
    public ChartData findDataForChartsById(String subscriptionId) {
        try {
            return clickhouseRepo.findDataForChartsById(subscriptionId);
        } catch (DataAccessException e) {
            log.warn("Error finding data for charts: {}",e.getMessage());
            return new ChartData();
        }
    }


    public Iterable<ClickhouseTransaction> findByIdWhereFilter(String subscriptionId, OnlineFilter filter) throws DataAccessException {

        try {
            return clickhouseRepo.findByIdWhereFilter(subscriptionId,filter);
        } catch (DataAccessException e) {
            log.warn("Error finding data for transactions: {}",e.getMessage());
            return new ArrayList<>();
        }
    }

    public Iterable<ClickhouseTransaction> findAllById(String subscriptionId) throws DataAccessException {

        try {
            return clickhouseRepo.findAllById(subscriptionId);
        } catch (DataAccessException e) {
            log.warn("Error finding data for transactions: {}",e.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteKafkaMaterialViewById(String id) throws DataAccessException{
        try {
            clickhouseRepo.deleteKafkaMaterialViewById(id);
        } catch (DataAccessException e) {
            log.warn("Error deleting Kafka's material view {}",e.getMessage());
        }
    }
    public void deleteTableById(String id) throws DataAccessException {

        try {
            clickhouseRepo.deleteTableById(id);
        } catch (DataAccessException e) {
            log.warn("Error deleting transactions table {}",e.getMessage());
        }
    }

    public void deleteKafkaById(String topicId) throws DataAccessException {

        try {
            clickhouseRepo.deleteKafkaById(topicId);
        } catch (DataAccessException e) {
            log.warn("Error deleting kafka table {}",e.getMessage());
        }
    }
    public void createTablesAfterSubscription(Subscription subscription, Filter filter) throws DataAccessException {

        try {
            clickhouseRepo.createTablesAfterSubscription(subscription,filter);
        } catch (DataAccessException e) {
            log.warn("Error creating tables {}",e.getMessage());
        }
    }
    public void createAnalysisTablesAfterSubscription(Subscription subscription) throws DataAccessException {

        try {
            clickhouseRepo.createAnalysisTablesAfterSubscription(subscription);
        } catch (DataAccessException e) {
            log.warn("Error creating analysis tables {}",e.getMessage());
        }
    }

    public void deleteSumTableById(String id) throws DataAccessException {

        try {
            clickhouseRepo.deleteSumTableById(id);
        } catch (DataAccessException e) {
            log.warn("Error deleting sum table {}",e.getMessage());
        }
    }

    public void deleteTableMaterialViewById(String id) throws DataAccessException {

        try {
            clickhouseRepo.deleteTableMaterialViewById(id);
        } catch (DataAccessException e) {
            log.warn("Error deleting material view table {}",e.getMessage());
        }
    }

    }

