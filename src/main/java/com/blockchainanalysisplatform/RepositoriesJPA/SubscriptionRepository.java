package com.blockchainanalysisplatform.RepositoriesJPA;

import com.blockchainanalysisplatform.Data.Subscription;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<Subscription,String>{ //CrudRepository<Subscription,String> {


    Optional<Subscription> findByTopicId(String topicId);

}
