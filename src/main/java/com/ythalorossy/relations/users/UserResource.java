package com.ythalorossy.relations.users;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "users")
public class UserResource {

    private UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAll() {

        List<UserDto> users = userService.getUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {

        UserDto byId = userService.getUser(id);

        return ResponseEntity.ok(byId);
    }

    @PostMapping
    public ResponseEntity<UserDto> persit(@RequestBody UserDto requestBody) {
        
        UserDto userDto = userService.createUSer(requestBody);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(userDto);
    }
    

    @PatchMapping("{id}/follow/{idToFollow}")
    public ResponseEntity<Void> postMethodName(@PathVariable Long id, @PathVariable Long idToFollow) {

        userService.followAnotherUser(id, idToFollow);

        return ResponseEntity.noContent().build();
    }

}
