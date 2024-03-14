package com.ythalorossy.relations.users;

import static org.mockito.ArgumentMatchers.anyLong;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserResource.class)
public class UserResourceMvcTest {

    private static final long _100L = 100L;
    private static final String YTHALO = "Ythalo";
    private static final String SALDANHA = "Saldanha";
    private static final String YTHALO_EMAIL = "ythalorossy@gmail.com";

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @Test
    public void getUserById() throws Exception {

        UserDto userDto = UserDto.builder().id(_100L).firstName(YTHALO).LastName(SALDANHA).email(YTHALO_EMAIL).build();

        Mockito.when(userService.getById(anyLong())).thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders
                .get("/users/100")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(_100L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(YTHALO))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(SALDANHA))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(YTHALO_EMAIL));
    }

}
