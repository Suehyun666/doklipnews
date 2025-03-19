package project.doklipnews.controller.dto.response;

import lombok.Getter;

@Getter
public class UserLoginResponse {
    private String message;
    private String errorCode;

    public UserLoginResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }
}

