package project.doklipnews.controller.dto.request;

import lombok.Getter;

@Getter
public class UpdateUserRequest {
    String username;
    String password;
    String email;
}
