package com.ythalorossy.relations.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {

    Optional<UserRelationship> findByFromUserAndToUser(User fromUser, User toUser);
}
