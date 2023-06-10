package com.blockchainanalysisplatform.services;

import com.blockchainanalysisplatform.Data.Filter;
import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.Repositories.JDBCClickhouseTransactionRepository;
import com.blockchainanalysisplatform.RepositoriesJPA.SubscriptionRepository;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import com.blockchainanalysisplatform.Services.EventeumService;
import com.blockchainanalysisplatform.Services.UsersSubscriptionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;


import static org.mockito.Mockito.*;

class UsersSubscriptionsServicesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private JDBCClickhouseTransactionRepository clickhouseTransactionRepository;

    @Mock
    private EventeumService eventeumService;

    private UsersSubscriptionsService usersSubscriptionsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        usersSubscriptionsService = new UsersSubscriptionsService(userRepository, subscriptionRepository,
                clickhouseTransactionRepository, eventeumService);
    }

    @Test
    void testDeleteSubscription_ShouldRemoveSubscriptionAndPerformCleanup() {
        // Arrange
        Long userId = 1L;
        String subscriptionId = "subId";

        User user = new User();
        user.setId(userId);
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.findByTopicId(subscription.getTopicId())).thenReturn(Optional.empty());

        // Act
        usersSubscriptionsService.deleteSubscription(subscriptionId, userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(subscriptionRepository, times(1)).findById(subscriptionId);
        verify(userRepository, times(1)).save(user);
        verify(subscriptionRepository, times(1)).deleteById(subscriptionId);
        verify(clickhouseTransactionRepository, times(1)).deleteKafkaMaterialViewById(subscriptionId);
        verify(clickhouseTransactionRepository, times(1)).deleteTableMaterialViewById(subscriptionId);
        verify(clickhouseTransactionRepository, times(1)).deleteSumTableById(subscriptionId);
        verify(clickhouseTransactionRepository, times(1)).deleteTableById(subscriptionId);
        verify(subscriptionRepository, times(1)).findByTopicId(subscription.getTopicId());
        verify(clickhouseTransactionRepository, times(1)).deleteKafkaById(subscription.getTopicId());
        verify(eventeumService, times(1)).unsubscribe(subscription.getTopicId());
    }

    @Test
    void testUpdatingUsersAndSubscriptions_ShouldAddNewUserAndCreateTables() {
        // Arrange
        Subscription subscription = new Subscription();
        User user = new User();
        Filter filter = new Filter();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(subscriptionRepository.findById(subscription.getId())).thenReturn(Optional.empty());

        // Act
        usersSubscriptionsService.updatingUsersAndSubscriptions(subscription, user, filter);

        // Assert
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
        verify(subscriptionRepository, times(1)).findById(subscription.getId());
        verify(userRepository, times(1)).save(user);
        verify(clickhouseTransactionRepository, times(1)).createTablesAfterSubscription(subscription, filter);
        verify(clickhouseTransactionRepository, times(1)).createAnalysisTablesAfterSubscription(subscription);
    }
}
