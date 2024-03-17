package com.ythalorossy.relations.users;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    Long id;
    String firstName;
    String LastName;
    String email;
    List<UserRelationshipDto> followers;
    List<UserRelationshipDto> following;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;
}

