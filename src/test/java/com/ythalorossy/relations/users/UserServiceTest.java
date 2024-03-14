package com.ythalorossy.relations.users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Mock
    UserRepository repository;

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
        final String email = "ythalorossy@gmail.com";
        final String firstName = "Ythalo";
        final String lastName = "Saldanha";
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> all = userService.getAll();

        assertThat(all, hasSize(1));
    }

}
