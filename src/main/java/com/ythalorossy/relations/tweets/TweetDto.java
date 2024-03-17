package com.ythalorossy.relations.tweets;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TweetDto {

    private Long id;
    private Long userId;
    private String tweetType;
    private String content;
    private LocalDateTime createdAt;

}
