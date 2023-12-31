package com.blockchainanalysisplatform.Services;

import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.RepositoriesJPA.SubscriptionRepository;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import com.blockchainanalysisplatform.Services.abstractions.UsersSubscriptionsInterface;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsersSubscriptionsService implements UsersSubscriptionsInterface {

    private UserRepository uRepo;
    private SubscriptionRepository sRepo;
    private ClickhouseService clickhouseService;
    private EventeumService unsubscribeEventeum;


    public void deleteSubscription(String subscriptionId, Long userId) {

//        User userDb = uRepo.findById(userId).get();
        User userDb = uRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found in delete function"));

        Optional<Subscription> optionalSubscription = sRepo.findById(subscriptionId);
        if (optionalSubscription.isPresent()) {
            Subscription existingSubscription = optionalSubscription.get();

            userDb.removeSubscription(existingSubscription);
            uRepo.save(userDb);

            if (existingSubscription.getUsers().isEmpty()) { //if after user deleting list of users is empty
                sRepo.deleteById(existingSubscription.getId());
                clickhouseService.deleteKafkaMaterialViewById(existingSubscription.getId());
                clickhouseService.deleteTableMaterialViewById(existingSubscription.getId());
                clickhouseService.deleteSumTableById(existingSubscription.getId());
                clickhouseService.deleteTableById(existingSubscription.getId());

                optionalSubscription = sRepo.findByTopicId(existingSubscription.getTopicId());
                if (optionalSubscription.isEmpty()) {//if in table after deleting subscription no entity with this address
                    clickhouseService.deleteKafkaById(existingSubscription.getTopicId());
                    unsubscribeEventeum.unsubscribe(existingSubscription.getTopicId());
                }
            }


        }
    }

    public void updatingUsersAndSubscriptions(Subscription subscription, User user, Filter filter) {

//        User userDb = uRepo.findById(user.getId()).get();
        User userDb = uRepo.findById(user.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found in update function"));
        if (!filter.isEmpty()) {
            subscription.setId(Hashing.sha256()
                    .hashString(subscription.getTopicId() + filter, StandardCharsets.UTF_8)
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
            clickhouseService.createTablesAfterSubscription(subscription, filter);
            clickhouseService.createAnalysisTablesAfterSubscription(subscription);

        }
    }
}
