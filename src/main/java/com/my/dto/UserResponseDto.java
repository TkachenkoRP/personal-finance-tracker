package com.my.dto;

import com.my.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
    private boolean blocked;
}
