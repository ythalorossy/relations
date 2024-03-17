package com.ythalorossy.relations.tweets;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TweetFavoriteDto {
    private Long id;
    private Long userId;
    private Long tweetId;
    private LocalDateTime createdAt;
}
