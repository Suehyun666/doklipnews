package project.doklipnews.controller.dto;

import lombok.Getter;

@Getter
public class UpdateUserRequest {
    String username;
    String password;
    String email;
}
