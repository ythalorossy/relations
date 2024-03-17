package com.ythalorossy.relations.users;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRelationshipDto {
    Long fromUser;
    Long toUser;
    LocalDateTime createdAt;
}

