package org.example.libdev.user.service;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.user.entity.User;
import org.example.libdev.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
@Slf4j
@Service
@RequiredArgsConstructor
public class OauthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String userId, email, userName, providerId;

        switch (provider) {
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                email = (String) kakaoAccount.get("email");
                userName = (String) profile.get("nickname");
                providerId = attributes.get("id").toString();
                userId = "KAKAO_" + providerId;
                break;

            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");

                email = (String) response.get("email");
                userName = (String) response.get("name");
                providerId = (String) response.get("id");
                userId = "NAVER_" + providerId;
                break;

            default:
                throw new RuntimeException("지원하지 않는 OAuth 제공자입니다.");
        }


        Optional<User> existingUser = userRepository.findByUserId(userId);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            String tempPassword = passwordEncoder.encode("OAuthUser");

            user = User.builder()
                    .userId(userId)
                    .email(email)
                    .userName(userName)
                    .password(tempPassword)
                    .auth("ROLE_USER")
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(user);
        }

        return new CustomOAuth2User(user.getUserId(), user.getEmail(), Set.of(new OAuth2UserAuthority("ROLE_USER", attributes)), attributes);
    }
}
