package project.doklipnews.controller.dto.request;

import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
}
