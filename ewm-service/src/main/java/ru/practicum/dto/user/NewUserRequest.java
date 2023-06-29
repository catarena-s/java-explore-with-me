
package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Данные нового пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "User name cannot be empty or null")
    @Size(min = 2, max = 250, message = "size must be between 2 and 250")
    private String name;

    @NotBlank(message = "Email cannot be empty or null")
    @Email(message = "Email must be valid")
    @Size(min = 6, max = 254, message = "size must be between 6 and 254")
    private String email;
}
