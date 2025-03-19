package project.doklipnews.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import project.doklipnews.controller.dto.request.CreateUserRequest;
import project.doklipnews.controller.dto.request.UpdateUserRequest;
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String name;
    @Email
    private String email;
    @NotNull
    private String password;

    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateName(UpdateUserRequest updateRequest) {
        this.name=updateRequest.getUsername();
    }
    public User(CreateUserRequest createUserRequest) {
        this.name=createUserRequest.getUsername();
        this.email=createUserRequest.getEmail();
        this.password=createUserRequest.getPassword();
    }
}
