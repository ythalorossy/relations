package com.ythalorossy.relations.users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "uuid")
    private String uuid;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at")
    private LocalDateTime modifedAt;

    @OneToMany(mappedBy = "fromUser", cascade = {CascadeType.MERGE})
    private List<UserRelationship> following = new ArrayList<>();

    @OneToMany(mappedBy = "toUser")
    private List<UserRelationship> followers = new ArrayList<>();

    public User(Long id, String firstName, String lastName, String email, String uuid) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uuid = uuid;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
