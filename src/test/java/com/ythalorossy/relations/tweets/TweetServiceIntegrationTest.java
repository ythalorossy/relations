package com.ythalorossy.relations.tweets;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ythalorossy.relations.users.User;
import com.ythalorossy.relations.users.UserService;

@SpringBootTest
@Testcontainers
public class TweetServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine").withDatabaseName("relations_tweet_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void postgresProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.passowrd", postgres::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    public void testPostgres() {
        assertThat(postgres)
                .isNotNull()
                .extracting(PostgreSQLContainer::isRunning).isEqualTo(true);
    }

    final String EMAIL = "ythalo@thatcompany.com";
    final String FIRST_NAME = "Ythalo";
    final String LAST_NAME = "Saldanha";
    final String CONTENT = "DUMMY TEXT";

    private User createUserForTest() {
        return new User(null, FIRST_NAME, LAST_NAME, EMAIL, null);
    }

    private List<TweetDto> createTweetsDtoForTest(Long userId, int numberOfTweets) {

        return IntStream.rangeClosed(1, numberOfTweets)
                .mapToObj(j -> TweetDto.builder()
                        .userId(userId)
                        .tweetType(TweetType.TEXT.name())
                        .content(String.format("%s_%d", CONTENT, j))
                        .createdAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    public void givenTweetDto_whenTweeting_thenTweetIsPersisted() {

        final User user = userService.persist(createUserForTest());

        TweetDto tweetDto = TweetDto.builder()
                .userId(user.getId())
                .tweetType(TweetType.TEXT.name())
                .content(CONTENT)
                .createdAt(LocalDateTime.now())
                .build();

        TweetDto tweet = tweetService.tweet(tweetDto);

        Assertions.assertThat(tweet)
                .extracting(TweetDto::getId, TweetDto::getUserId, TweetDto::getTweetType, TweetDto::getContent)
                .doesNotContainNull();
        Assertions.assertThat(tweet)
                .extracting(TweetDto::getTweetType, TweetDto::getContent)
                .contains(TweetType.TEXT.name(), CONTENT);
    }

    @Test
    public void givenUserId_whenRetrievingAllTweets_theReturnListOfTweets() {

        final User user = userService.persist(createUserForTest());

        final int numberOfTweets = 5;
        createTweetsDtoForTest(user.getId(), numberOfTweets).forEach(tweetService::tweet);

        List<TweetDto> allTweetsByUserId = tweetService.getAllTweetsByUserId(user.getId());

        assertThat(allTweetsByUserId).size().isEqualTo(5);
        assertThat(allTweetsByUserId)
                .hasSize(numberOfTweets)
                .allMatch(t -> t.getTweetType().equals(TweetType.TEXT.name()))
                .allMatch(t -> t.getContent().matches(String.format("%s_\\d+", CONTENT)));
    }

}
