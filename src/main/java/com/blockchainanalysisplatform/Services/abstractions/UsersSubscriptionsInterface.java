package com.blockchainanalysisplatform.Services.abstractions;

import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.User;

public interface UsersSubscriptionsInterface {
    void deleteSubscription(String subscriptionId, Long userId);

    void updatingUsersAndSubscriptions(Subscription subscription, User user, Filter filter);
}
