package com.ythalorossy.relations.users;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    final String EMAIL = "dummy@dummy.com";
    final String FIRST_NAME = "Dummy";
    final String LAST_NAME = "AndYummy";

    @Mock
    UserRepository repository;

    @Mock
    UserRelationshipRepository userRelationshipRepository;

    @InjectMocks
    UserService userService;

    @Test
    public void givenValidUserID_whenLookingForUser_thenReturnUSerDto() {

        final long id = 10L;
        final String email = "ythalorossy@gmail.com";
        final String firstName = "Ythalo";
        final String lastName = "Saldanha";

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(id);
        assertNotNull(userDto);
        assertThat(userDto, allOf(
            hasProperty("firstName", equalTo(firstName)),
            hasProperty("lastName", equalTo(lastName)),
            hasProperty("email", equalTo(email))
        ));

        assertThat(userDto.getFollowers(), is(empty()));
        assertThat(userDto.getFollowing(), is(empty()));
    }

    @Test
    public void givenInvalidUserID_whenLookingForUser_thenReturnException() {

        final long id = 10L;

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.getById(id));
    }

    @Test
    public void whenLookingForAllUser_thenReturnListUserDto() {
        final long id = 10L;
        User user = createUserForTest(id);

        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> all = userService.getAll();

        assertThat(all, hasSize(1));
    }

    @SuppressWarnings("null")
    @Test
    public void givenUserDto_whenFollowing_thenFollowIsDone() {

        final long id = 10L;
        User user = createUserForTest(id);

        final long id_2 = 20L;
        User user_2 = createUserForTest(id_2);

        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setFromUser(user);
        userRelationship.setToUser(user_2);
        userRelationship.setId(999L);

        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.findById(id_2)).thenReturn(Optional.of(user_2));
        when(userRelationshipRepository.findByFromUserAndToUser(user, user_2))
            .thenReturn(Optional.empty());
        
        userService.follow(id, id_2);
        verify(repository, atLeast(2)).findById(anyLong());
        verify(userRelationshipRepository, atLeastOnce()).findByFromUserAndToUser(user, user_2);
        verify(repository, atLeastOnce()).save(any(User.class));
    }

    @SuppressWarnings("null")
    @Test
    public void givenUserDto_whenUserAlreadyFollowing_thenThrowException() {

        final long id = 10L;
        User user = createUserForTest(id);

        final long id_2 = 20L;
        User user_2 = createUserForTest(id_2);

        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setFromUser(user);
        userRelationship.setToUser(user_2);
        userRelationship.setId(999L);

        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.findById(id_2)).thenReturn(Optional.of(user_2));
        when(userRelationshipRepository.findByFromUserAndToUser(user, user_2))
            .thenReturn(Optional.of(userRelationship));
        
        assertThatThrownBy(()->userService.follow(id, id_2))
            .isInstanceOf(UserException.class);

        verify(repository, atLeast(2)).findById(anyLong());
        verify(userRelationshipRepository, atLeastOnce()).findByFromUserAndToUser(user, user_2);
        verify(repository, never()).save(any(User.class));
    }

    private User createUserForTest(final long id) {
        User user = new User();
        user.setId(id);
        user.setEmail(format("%s_%d", EMAIL, id));
        user.setFirstName(format("%s_%d", FIRST_NAME, id));
        user.setLastName(format("%s_%d", LAST_NAME, id));
        return user;
    }

}
