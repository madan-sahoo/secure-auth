package com.secureauth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private boolean isUserActive = true;

    private boolean accountNonLocked = true;

    private int failedAttempts = 0;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserRole> roles;

    private Instant createdAt = Instant.now();

    private String name;

    private Date birthday;

    // constructor: create user
    public User(String username, String email, String passwordHash, String name, Date birthday) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.failedAttempts = 0;
        this.name = name;
        this.birthday = birthday;
    }
}