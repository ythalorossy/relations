package com.ythalorossy.relations.tweets;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ythalorossy.relations.users.User;
import com.ythalorossy.relations.users.UserException;
import com.ythalorossy.relations.users.UserService;

@Service
public class TweetService {

    private TweetRepository tweetRepository;
    private UserService userService;

    public TweetService(TweetRepository tweetRepository, UserService userService) {
        this.tweetRepository = tweetRepository;
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TweetDto tweet(TweetDto tweetDto) {

        User user = userService.getUser(tweetDto.getUserId());

        Tweet tweet = new Tweet();
        tweet.setType(TweetType.valueOf(tweetDto.getTweetType()));
        tweet.setContent(tweetDto.getContent());
        tweet.setUser(user);
        tweet.setCreateAt(Optional.ofNullable(tweetDto.getCreatedAt()).orElse(LocalDateTime.now()));

        final Tweet tweetPersisted = tweetRepository.save(tweet);

        return convertToDto(tweetPersisted);
    }

    public TweetDto getById(Long tweetId) {

        if (tweetId == null)
            throw new UserException(String.format("Tweet ID cannot be empty"));

        final Tweet tweet = tweetRepository
                .findById(tweetId)
                .orElseThrow(() -> new UserException(String.format("Tweed %d not found", tweetId)));

        return convertToDto(tweet);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TweetDto> getAllTweetsByUserId(Long userId) {

        User user = userService.getUser(userId);

        List<Tweet> tweets = tweetRepository.findAllByUser(user);

        return tweets.stream().map(this::convertToDto).collect(Collectors.toList());
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
}
