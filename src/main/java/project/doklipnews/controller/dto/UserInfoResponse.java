package project.doklipnews.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserInfoResponse {
    private String username;
    private String email;

    public UserInfoResponse(@Email String email, @NotNull String name) {
        this.username = name;
        this.email = email;
    }
}
