package org.example.libdev.user.service;

import org.example.libdev.user.dto.UserRequestDto;
import org.example.libdev.user.dto.UserLoginDto;
import org.example.libdev.user.entity.User;
import org.example.libdev.user.security.JwtUtil;
import org.example.libdev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final @Lazy UserRepository userRepository;
    private final @Lazy PasswordEncoder passwordEncoder;
    private final @Lazy JwtUtil jwtUtil;
    private final @Lazy AuthCodeService authCodeService;


    public String register(UserRequestDto requestDto) {
        if (userRepository.findByUserId(requestDto.getUserId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자 ID입니다.");
        }
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }
        if (userRepository.findByPhone(requestDto.getPhone()).isPresent()) {
            throw new RuntimeException("이미 등록된 전화번호입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .userId(requestDto.getUserId())
                .password(encodedPassword)
                .userName(requestDto.getUserName())
                .email(requestDto.getEmail())
                .phone(requestDto.getPhone())
                .auth("ROLE_USER")
                .build();

        userRepository.save(user);
        return "회원가입 성공!";
    }

    public String login(UserLoginDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(user.getUserId());
    }

    public String findUserIdByNameAndEmail(String userName, String email) {
        User user = userRepository.findByUserNameAndEmail(userName, email)
                .orElseThrow(() -> new IllegalArgumentException("입력한 이름과 이메일이 일치하는 계정을 찾을 수 없습니다."));
        return user.getUserId();
    }

    public void resetPasswordWithCode(String userId, String email, String code, String newPassword) {
        // 아이디와 이메일이 일치하는 사용자 찾기
        User user = userRepository.findByUserIdAndEmail(userId, email)
                .orElseThrow(() -> new IllegalArgumentException("입력한 아이디와 이메일이 일치하는 계정이 없습니다."));

        // 인증번호 확인
        if (!authCodeService.verifyCode(email, code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // 인증번호 사용 후 삭제
        authCodeService.removeCode(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
