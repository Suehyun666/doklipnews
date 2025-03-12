package project.doklipnews.controller.dto;

import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
}
