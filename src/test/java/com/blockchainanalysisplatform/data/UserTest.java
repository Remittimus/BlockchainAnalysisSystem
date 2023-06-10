package com.blockchainanalysisplatform.data;

import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "testuser@example.com");
        subscription = new Subscription();
    }

    @Test
    void testAddNewSubscription_ShouldAddSubscriptionToList() {
        // Arrange
        assertEquals(0, user.getSubscriptions().size());

        // Act
        user.addNewSubscription(subscription);

        // Assert
        assertEquals(1, user.getSubscriptions().size());
        assertTrue(user.getSubscriptions().contains(subscription));
        assertTrue(subscription.getUsers().contains(user));
    }

    @Test
    void testRemoveSubscription_ShouldRemoveSubscriptionFromList() {
        // Arrange
        user.addNewSubscription(subscription);
        assertEquals(1, user.getSubscriptions().size());

        // Act
        user.removeSubscription(subscription);

        // Assert
        assertEquals(0, user.getSubscriptions().size());
        assertFalse(user.getSubscriptions().contains(subscription));
        assertFalse(subscription.getUsers().contains(user));
    }

    @Test
    void testRemoveSubscription_ShouldNotRemoveSubscriptionIfNotPresent() {
        // Arrange
        Subscription otherSubscription = new Subscription();
        otherSubscription.setId("123456");
        user.addNewSubscription(subscription);
        assertEquals(1, user.getSubscriptions().size());

        // Act
        user.removeSubscription(otherSubscription);

        // Assert
        assertEquals(1, user.getSubscriptions().size());
        assertTrue(user.getSubscriptions().contains(subscription));
        assertTrue(subscription.getUsers().contains(user));
    }
}
