package com.secureauth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // ADMIN, USER

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<RolePermission> permissions;
}