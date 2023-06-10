package com.blockchainanalysisplatform.data;

import com.blockchainanalysisplatform.Data.Subscription;
import com.blockchainanalysisplatform.Data.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {

    private Subscription subscription;
    private User user;

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        user = new User("testuser", "password", "testuser@example.com");
    }

    @Test
    void testAddNewUser_ShouldAddUserToList() {
        // Arrange
        assertEquals(0, subscription.getUsers().size());

        // Act
        subscription.addNewUser(user);

        // Assert
        assertEquals(1, subscription.getUsers().size());
        assertTrue(subscription.getUsers().contains(user));
        assertTrue(user.getSubscriptions().contains(subscription));
    }

    @Test
    void testRemoveUser_ShouldRemoveUserFromList() {
        // Arrange
        subscription.addNewUser(user);
        assertEquals(1, subscription.getUsers().size());

        // Act
        subscription.removeUser(user);

        // Assert
        assertEquals(0, subscription.getUsers().size());
        assertFalse(subscription.getUsers().contains(user));
        assertFalse(user.getSubscriptions().contains(subscription));
    }

    @Test
    void testRemoveUser_ShouldNotRemoveUserIfNotPresent() {
        // Arrange
        User otherUser = new User("otheruser", "password", "otheruser@example.com");
        subscription.addNewUser(user);
        assertEquals(1, subscription.getUsers().size());

        // Act
        subscription.removeUser(otherUser);

        // Assert
        assertEquals(1, subscription.getUsers().size());
        assertTrue(subscription.getUsers().contains(user));
        assertTrue(user.getSubscriptions().contains(subscription));
    }
}
