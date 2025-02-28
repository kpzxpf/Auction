package com.volzhin.auction.dto;

import com.volzhin.auction.entity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {
    private Long id;

    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 120)
    private String username;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, max = 120)
    private String password;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Incorrect email format")
    private String email;

    private BigDecimal balance;

    private User.Role role;
}
