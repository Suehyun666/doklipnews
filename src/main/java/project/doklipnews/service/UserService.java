package project.doklipnews.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.doklipnews.controller.dto.request.CreateUserRequest;
import project.doklipnews.controller.dto.request.UpdateUserRequest;
import project.doklipnews.controller.dto.request.UserLoginRequest;
import project.doklipnews.controller.dto.response.UserInfoResponse;
import project.doklipnews.controller.dto.response.UserLoginResponse;
import project.doklipnews.entity.User;
import project.doklipnews.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean signup(CreateUserRequest createUserRequest) {
        if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent()) {
            return false;
        }
        String encodedPassword = passwordEncoder.encode(createUserRequest.getPassword());
        User user = new User(createUserRequest);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return true;
    }

    public UserInfoResponse updateUser(String userEmail, UpdateUserRequest updateRequest) {
        User user = findUserByEmail(userEmail);
        user.updateName(updateRequest);
        return getUserInfo(userEmail);
    }

    public UserInfoResponse deleteUserByEmail(String userEmail) {
        UserInfoResponse userInfo = getUserInfo(userEmail);
        userRepository.deleteByEmail(userEmail);
        return userInfo;
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User currentUser = findUserByEmail(userLoginRequest.getEmail());
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), currentUser.getPassword())) {
            throw new RuntimeException("잘못된 비밀번호입니다.");
        }
        return new UserLoginResponse("로그인 성공","jwtUtil.createJwt(userLoginRequest.getEmail())");
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userEmail) {
        User user = findUserByEmail(userEmail);
        return new UserInfoResponse(user.getEmail(),user.getName());
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("해당하는 아이디가 없습니다."));
    }
}
