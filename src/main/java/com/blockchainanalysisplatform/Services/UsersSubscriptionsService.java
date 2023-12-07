package com.blockchainanalysisplatform.Services;

import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.Repositories.JDBCClickhouseTransactionRepository;
import com.blockchainanalysisplatform.RepositoriesJPA.SubscriptionRepository;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsersSubscriptionsService implements UsersSubscriptionsInterface{

    private UserRepository uRepo;
    private SubscriptionRepository sRepo;
    private JDBCClickhouseTransactionRepository clickRepo;
    private EventeumService unsubscribeEventeum;


    public void deleteSubscription(String subscriptionId,Long userId){


        User userDb = uRepo.findById(userId).get();

        Optional<Subscription> optionalSubscription = sRepo.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            Subscription existingSubscription = optionalSubscription.get();

            userDb.removeSubscription(existingSubscription);
            uRepo.save(userDb);

            if(existingSubscription.getUsers().isEmpty()){ //if after user deleting list of users is empty
                sRepo.deleteById(existingSubscription.getId());
                clickRepo.deleteKafkaMaterialViewById(existingSubscription.getId());
                clickRepo.deleteTableMaterialViewById(existingSubscription.getId());
                clickRepo.deleteSumTableById(existingSubscription.getId());
                clickRepo.deleteTableById(existingSubscription.getId());

                optionalSubscription = sRepo.findByTopicId(existingSubscription.getTopicId());
                if(optionalSubscription.isEmpty()){//if in table after deleting subscription no entity with this address
                    clickRepo.deleteKafkaById(existingSubscription.getTopicId());
                    unsubscribeEventeum.unsubscribe(existingSubscription.getTopicId());
                }
            }


        }
    }

    public void updatingUsersAndSubscriptions(Subscription subscription, User user, Filter filter){

        //User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //Optional<User>  optionalUser= uRepo.findById(user.getId());
            User userDb = uRepo.findById(user.getId()).get();
            if(!filter.isEmpty()){
                subscription.setId(Hashing.sha256()
                        .hashString(subscription.getTopicId()+filter, StandardCharsets.UTF_8)
                        .toString());

            }

            Optional<Subscription> optionalSubscription = sRepo.findById(subscription.getId());
            if (optionalSubscription.isPresent()) {
                Subscription existingSubscription = optionalSubscription.get();


                if (!existingSubscription.getUsers().contains(userDb)) { //if other user create this subscription


                    existingSubscription.addNewUser(userDb);

                    uRepo.save(userDb);

                }
            } else { //if its new subscription for all users

                userDb.addNewSubscription(subscription);
                uRepo.save(userDb);
                clickRepo.createTablesAfterSubscription( subscription,filter);
                clickRepo.createAnalysisTablesAfterSubscription(subscription);

            }


    }
}
