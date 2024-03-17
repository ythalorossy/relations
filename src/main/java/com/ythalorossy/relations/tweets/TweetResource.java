package com.ythalorossy.relations.tweets;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(path = "tweets")
public class TweetResource {

    private TweetService tweetService;

    public TweetResource(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetDto> createTweet(@RequestBody TweetDto tweetDto) {

        TweetDto response = tweetService.tweet(tweetDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetDto> getTweet(@PathVariable Long id) {

        TweetDto tweetDto = tweetService.getTweet(id);

        return ResponseEntity.ok(tweetDto);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TweetDto>> getTweetsByUser(@PathVariable Long userId) {

        List<TweetDto> tweets = tweetService.getTweetsByUser(userId);

        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/user/{id}/favorites")
    public ResponseEntity<List<TweetFavoriteDto>> getFavoriteTweetsByUser(@PathVariable Long userId) {

        List<TweetFavoriteDto> tweets = tweetService.getFavoriteTweetByUser(userId);

        return ResponseEntity.ok(tweets);
    }

    @PostMapping("/favorites")
    public ResponseEntity<TweetFavoriteDto> favoriteTweet(@RequestBody TweetFavoriteDto tweetFavoriteDto) {

        final Long userId = tweetFavoriteDto.getUserId();
        final Long tweetId = tweetFavoriteDto.getTweetId();

        TweetFavoriteDto response = tweetService.setTweetAsFavorite(userId, tweetId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/favorites/{favoriteId}")
    public ResponseEntity<TweetFavoriteDto> getFavoriteTweet(@PathVariable Long favoriteId) {

        TweetFavoriteDto response = tweetService.getFavoriteTweet(favoriteId);

        return ResponseEntity.ok(response);
    }

}
