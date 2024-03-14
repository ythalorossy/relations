package com.ythalorossy.relations.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(SpringExtension.class)
public class UserResourceTest {

    private static final long _100L = 100L;
    private static final String YTHALO = "Ythalo";
    private static final String SALDANHA = "Saldanha";
    private static final String YTHALO_EMAIL = "ythalorossy@gmail.com";

    @InjectMocks
    UserResource userResource;

    @Mock
    UserService userService;

    @BeforeAll
    public static void init() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test   // Using assertj
    public void givenUserDto_whenPersisting_thenUserDtoPersisted() {

        // Mock dependencies    
        UserDto userDto = UserDto.builder().id(_100L).firstName(YTHALO).LastName(SALDANHA).email(YTHALO_EMAIL).build();
        when(userService.persist(any(UserDto.class))).thenReturn(userDto);
        
        // Call Resource
        UserDto userDtoRequest = UserDto.builder().firstName(YTHALO).LastName(SALDANHA).email(YTHALO_EMAIL).build();
        ResponseEntity<UserDto> responseEntity = userResource.persit(userDtoRequest);

        // Assertions
        assertThat(responseEntity).isNotNull()
                .extracting(ResponseEntity::getHeaders).isNotNull()
                .extracting(HttpHeaders::getLocation).isNotNull()
                .extracting(URI::getPath).isEqualTo(String.format("/%d", _100L));

        assertThat(responseEntity)
            .extracting(ResponseEntity::getBody).isNotNull()
            .extracting("id", "firstName", "lastName", "email")
            .contains(100L, YTHALO, SALDANHA, YTHALO_EMAIL);
    }
}
