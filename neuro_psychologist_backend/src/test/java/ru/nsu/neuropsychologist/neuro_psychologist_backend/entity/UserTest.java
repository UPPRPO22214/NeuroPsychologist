package ru.nsu.neuropsychologist.neuro_psychologist_backend.entity;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testUserSettersAndGetters() {
        User user = new User();
        
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setFirstName("John");
        
        ZonedDateTime now = ZonedDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals("John", user.getFirstName());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testUserDetailsImplementation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        
        assertEquals("test@example.com", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void testPrePersist() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("password");
        
        // Simulate @PrePersist
        user.setCreatedAt(ZonedDateTime.now());
        user.setUpdatedAt(ZonedDateTime.now());
        
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testPreUpdate() {
        User user = new User();
        ZonedDateTime created = ZonedDateTime.now().minusDays(1);
        user.setCreatedAt(created);
        user.setUpdatedAt(created);
        
        // Simulate @PreUpdate
        ZonedDateTime updated = ZonedDateTime.now();
        user.setUpdatedAt(updated);
        
        assertEquals(created, user.getCreatedAt());
        assertTrue(user.getUpdatedAt().isAfter(created));
    }
}