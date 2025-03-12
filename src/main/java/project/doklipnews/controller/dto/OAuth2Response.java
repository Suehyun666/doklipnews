package project.doklipnews.controller.dto;

public interface OAuth2Response {

    // 제공자
    String getProvider();

    // 제공자에서 발급해준 ID
    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 실명
    String getName();

    // 프로필 이미지
    String getProfileImage();
}
