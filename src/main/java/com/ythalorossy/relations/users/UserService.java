package com.ythalorossy.relations.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserRelationshipRepository userRelationshipRepository;

    public UserService(UserRepository userRepository, UserRelationshipRepository userRelationshipRepository) {
        this.userRepository = userRepository;
        this.userRelationshipRepository = userRelationshipRepository;
    }

    public UserDto getById(Long userId) {

        final User user = getUser(userId);

        return convertToDto(user);
    }

    public User getUser(Long userId) {

        if (userId == null)
            throw new UserException(String.format("User ID cannot be empty"));

        final User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserException(String.format("User %d not found", userId)));
                
        return user;
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
        if (user.getId() == null) {
            user.setUuid(UUID.randomUUID().toString());
            user.setCreatedAt(LocalDateTime.now());
        }

        user.setModifedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void follow(Long id, Long idToFollow) {

        if (id == null)
            throw new UserException(String.format("User ID cannot be empty"));

        if (idToFollow == null)
            throw new UserException(String.format("User ID to Follow cannot be empty"));
            
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserException(String.format("User %d not found", id)));
        
        User userToFollow = userRepository.findById(idToFollow)
            .orElseThrow(() -> new UserException(String.format("User %d not found", idToFollow)));
        
        userRelationshipRepository.findByFromUserAndToUser(user, userToFollow)
            .ifPresent(t -> {
                throw new UserException(String.format("User %d already follows user %d", t.getFromUser().getId(), t.getToUser().getId()));
            });

        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setFromUser(user);
        userRelationship.setToUser(userToFollow);
        userRelationship.setCreatedAt(LocalDateTime.now());

        user.getFollowing().add(userRelationship);

        user.setModifedAt(LocalDateTime.now());

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
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifedAt())
                .build();
    }

    private UserRelationshipDto convertToDto(UserRelationship userRelationship) {
        return UserRelationshipDto.builder()
                .fromUser(userRelationship.getFromUser().getId())
                .toUser(userRelationship.getToUser().getId())
                .createdAt(userRelationship.getCreatedAt())
                .build();
    }
}
