package com.ythalorossy.relations.users;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SuppressWarnings("null")
    public UserDto getById(Long id) {

        final User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserException(String.format("User %d not found", id)));

        return convertToDto(user);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return convertToDto(users);
    }

    public UserDto persist(UserDto dto) {
        User user = this.persist(convertToEntity(dto));
        return convertToDto(user);
    }

    public User persist(User user) {
        user.setUuid(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void follow(Long id, Long idToFollow) {

        if (id == null)
            throw new UserException(String.format("User ID cannot be empty"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(String.format("User %d not found", id)));

        if (idToFollow == null)
            throw new UserException(String.format("User ID to Follow cannot be empty"));

        User userToFollow = userRepository.findById(idToFollow)
                .orElseThrow(() -> new UserException(String.format("User %d not found", idToFollow)));

        user.getFollowing().add(userToFollow);
        userToFollow.getFollowers().add(user);

        userRepository.save(user);
        userRepository.save(userToFollow);
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        return user;
    }

    private List<UserDto> convertToDto(List<User> users) {
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        
        final List<UserDto> following = 
                user.getFollowing().stream().map(this::shallowUserDto).collect(Collectors.toList());
        
        final List<UserDto> followers = 
                user.getFollowers().stream().map(this::shallowUserDto).collect(Collectors.toList());
        
        UserDto userDto = shallowUserDto(user);
        userDto.setFollowers(followers);
        userDto.setFollowing(following);

        return userDto;
    }

    private UserDto shallowUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .LastName(user.getLastName())
                .email(user.getEmail())
                .followers(Collections.emptyList())
                .following(Collections.emptyList())
                .build();
    }

}
