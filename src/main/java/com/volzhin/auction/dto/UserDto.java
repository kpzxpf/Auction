package com.volzhin.auction.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {
    @NotBlank(message = "Username не должен быть пустым")
    @Size(min = 3, max = 120)
    private String username;
    @NotBlank(message = "Password не должен быть пустым")
    @Size(min = 8, max = 120)
    private String password;
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
}
