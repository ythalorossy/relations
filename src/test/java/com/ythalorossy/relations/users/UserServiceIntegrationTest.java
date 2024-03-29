package com.ythalorossy.relations.users;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.Table;

@SpringBootTest
@Testcontainers
public class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:15-alpine"
    ).withDatabaseName("relations_test")
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
    private JdbcTemplate jdbcTemplate;

    @Test
    void test() {
        assertThat(postgres, notNullValue());
        assertThat(postgres.isRunning(), equalTo(true));
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @SuppressWarnings("null")
    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, 
            UserRelationship.class.getAnnotationsByType(Table.class)[0].name(),
            User.class.getAnnotationsByType(Table.class)[0].name());
    }

    @Test
    public void givenUser_whenPersisting_thenUserIsPersisted() {

        final String email = "ythalorossy@gmail.com";
        final String firstName = "Ythalo";
        final String lastName = "Saldanha";

        final User user = new User(null, firstName, lastName, email, null);

        User persistedUser = userService.persist(user);

        assertThat(persistedUser, notNullValue());
        assertThat(persistedUser.getId(), notNullValue());
        assertThat(persistedUser.getFirstName(), equalTo(firstName));
        assertThat(persistedUser.getLastName(), equalTo(lastName));
        assertThat(persistedUser.getEmail(), equalTo(email));
    }

    @Test
    public void givenUser_whenAddingFollowing_thenUserIsPersisted() {

        final String email = "ythalorossy@gmail.com";
        final String firstName = "Ythalo";
        final String lastName = "Saldanha";

        final User user = userService.persist(new User(null, firstName, lastName, email, null));
        
        final String email1 = "luanna@gmail.com";
        final String firstName1 = "Luanna";
        final String lastName1 = "Fal";        
        
        final User userToFollow = userService.persist(new User(null, firstName1, lastName1, email1, null));

        userService.followAnotherUser(user.getId(), userToFollow.getId());

        assertThat(user, notNullValue());
        assertThat(user.getId(), notNullValue());
        assertThat(user.getFirstName(), equalTo(firstName));
        assertThat(user.getLastName(), equalTo(lastName));
        assertThat(user.getEmail(), equalTo(email));
    }

}
