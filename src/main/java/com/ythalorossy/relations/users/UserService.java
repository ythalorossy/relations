package com.ythalorossy.relations.users;

import java.time.LocalDateTime;
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

        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setFromUser(user);
        userRelationship.setToUser(userToFollow);
        userRelationship.setSince(LocalDateTime.now());

        user.getFollowing().add(userRelationship);

        userRepository.save(user);
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

        final List<UserRelationshipDto> following = user.getFollowing().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        final List<UserRelationshipDto> followers = user.getFollowers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .LastName(user.getLastName())
                .email(user.getEmail())
                .following(following)
                .followers(followers)
                .build();
    }

    private UserRelationshipDto convertToDto(UserRelationship userRelationship) {
        return UserRelationshipDto.builder()
                .fromUser(userRelationship.getFromUser().getId())
                .toUser(userRelationship.getToUser().getId())
                .since(userRelationship.getSince())
                .build();
    }
}
