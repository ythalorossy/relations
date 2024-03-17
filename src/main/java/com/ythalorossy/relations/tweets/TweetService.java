package com.ythalorossy.relations.tweets;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ythalorossy.relations.users.User;
import com.ythalorossy.relations.users.UserService;

@Service
public class TweetService {

    private TweetRepository tweetRepository;
    private UserService userService;
    private TweetFavoriteRepository tweetFavoriteRepository;

    public TweetService(TweetRepository tweetRepository, UserService userService,
            TweetFavoriteRepository tweetFavoriteRepository) {
        this.tweetRepository = tweetRepository;
        this.userService = userService;
        this.tweetFavoriteRepository = tweetFavoriteRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TweetDto tweet(TweetDto tweetDto) {

        User user = userService.retrieveUser(tweetDto.getUserId());

        Tweet tweet = new Tweet();
        tweet.setType(TweetType.valueOf(tweetDto.getTweetType()));
        tweet.setContent(tweetDto.getContent());
        tweet.setUser(user);
        tweet.setCreateAt(Optional.ofNullable(tweetDto.getCreatedAt()).orElse(LocalDateTime.now()));

        final Tweet tweetPersisted = tweetRepository.save(tweet);

        return convertToDto(tweetPersisted);
    }

    public TweetDto getTweet(Long tweetId) {

        final Tweet tweet = retrieveTweet(tweetId);

        return convertToDto(tweet);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TweetDto> getTweetsByUser(Long userId) {

        User user = userService.retrieveUser(userId);

        List<Tweet> tweets = tweetRepository.findAllByUser(user);

        return tweets.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public TweetFavoriteDto setTweetAsFavorite(Long userId, Long tweetId) {

        User user = userService.retrieveUser(userId);

        Tweet tweet = retrieveTweet(tweetId);

        TweetFavorite tweetFavorite = new TweetFavorite();
        tweetFavorite.setUser(user);
        tweetFavorite.setTweet(tweet);
        tweetFavorite.setCreatedAt(LocalDateTime.now());

        tweetFavoriteRepository.save(tweetFavorite);

        return convertTweetFavoriteToDto(tweetFavorite);
    }

    public TweetFavoriteDto getFavoriteTweet(Long favoriteId) {

        TweetFavorite tweetFavorite = retrieveTweetFavorite(favoriteId);

        return convertTweetFavoriteToDto(tweetFavorite);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TweetFavoriteDto> getFavoriteTweetByUser(Long userId) {

        User user = userService.retrieveUser(userId);

        List<TweetFavorite> tweetFavorites = tweetFavoriteRepository.findAllByUser(user);

        return tweetFavorites.stream().map(this::convertTweetFavoriteToDto).collect(Collectors.toList());
    }

    private TweetFavorite retrieveTweetFavorite(Long favoriteId) {
        if (favoriteId == null)
            throw new TweetException(String.format("Tweet Favoritre ID cannot be empty"));

        TweetFavorite tweetFavorite = tweetFavoriteRepository
                .findById(favoriteId)
                .orElseThrow(() -> new TweetException(String.format("Tweet Favorite %d not found", favoriteId)));
        return tweetFavorite;
    }

    private Tweet retrieveTweet(Long tweetId) {
        if (tweetId == null)
            throw new TweetException(String.format("Tweet ID cannot be empty"));

        final Tweet tweet = tweetRepository
                .findById(tweetId)
                .orElseThrow(() -> new TweetException(String.format("Tweet %d not found", tweetId)));
        return tweet;
    }

    private TweetDto convertToDto(Tweet tweet) {

        return TweetDto.builder()
                .id(tweet.getId())
                .tweetType(tweet.getType().name())
                .content(tweet.getContent())
                .createdAt(tweet.getCreateAt())
                .userId(tweet.getUser().getId())
                .build();
    }

    private TweetFavoriteDto convertTweetFavoriteToDto(TweetFavorite tweetFavorite) {

        return TweetFavoriteDto.builder()
                .id(tweetFavorite.getId())
                .userId(tweetFavorite.getUser().getId())
                .tweetId(tweetFavorite.getTweet().getId())
                .createdAt(tweetFavorite.getCreatedAt())
                .build();
    }
}
