package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_d_type")
@Table(name = "Users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique=true)
    private String username;
    private String password;
    private String roles;
    private boolean isAccountNonLocked = true;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant lastUpdatedAt;

    //@Column(insertable = false, updatable = false)
    private UserType userType;

    public User(String name, String username, String password, String roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

}
