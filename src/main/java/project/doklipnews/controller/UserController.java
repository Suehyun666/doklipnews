package project.doklipnews.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import project.doklipnews.controller.dto.CreateUserRequest;
import project.doklipnews.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/login")
    public String getlogin() {
        return "login";
    }
    @PostMapping("/login")
    public String login() {

        return "/articles";
    }
    @GetMapping("/signup")
    public String getsignup() {
        return "signup";
    }
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody CreateUserRequest createUserRequest) {
        if(userService.signup(createUserRequest)){
            return "redirect:/articles";
        }
        return "/login";
    }
    @GetMapping("/password")
    public String getpassword() {
        return "password";
    }
    @PostMapping("/password")
    public String password(@RequestParam String id) {
        if(id!=null) {
            return "redirect:/articles/";
        }
        return "/login";
    }
}
