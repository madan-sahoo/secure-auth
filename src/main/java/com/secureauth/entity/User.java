package com.secureauth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private boolean enabled = true;

    private boolean accountNonLocked = true;

    private int failedAttempts = 0;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserRole> roles;

    private Instant createdAt = Instant.now();
}