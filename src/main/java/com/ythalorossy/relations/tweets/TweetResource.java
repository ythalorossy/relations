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
    public ResponseEntity<TweetDto> getTweetById(@PathVariable Long id) {

        TweetDto tweetDto = tweetService.getById(id);

        return ResponseEntity.ok(tweetDto);
    }

    
    @GetMapping("/user/{id}")
    public ResponseEntity<List<TweetDto>> getAllTweetByUser(@PathVariable Long id) {

        List<TweetDto> tweets = tweetService.getAllTweetsByUserId(id);

        return ResponseEntity.ok(tweets);
    }

}
