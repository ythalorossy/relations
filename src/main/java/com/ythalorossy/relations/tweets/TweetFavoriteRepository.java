package com.ythalorossy.relations.tweets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythalorossy.relations.users.User;

public interface TweetFavoriteRepository extends JpaRepository<TweetFavorite, Long>{

    List<TweetFavorite> findAllByUser(User user);

}
