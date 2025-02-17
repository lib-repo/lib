package org.example.libdev.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.example.libdev.user.entity.User;
import org.example.libdev.user.repository.UserRepository;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthCodeService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // 인증번호 생성 및 저장
    public String generateVerificationCode(String email) {
        String code = String.format("%06d", random.nextInt(999999)); // 6자리 랜덤 숫자 생성
        verificationCodes.put(email, code);
        return code;
    }

    // 인증번호 이메일 전송
    public void sendVerificationCode(String userId, String email) {
        User user = userRepository.findByUserIdAndEmail(userId, email)
                .orElseThrow(() -> new IllegalArgumentException("입력한 아이디와 이메일이 일치하는 계정이 없습니다."));

        String code = generateVerificationCode(email);

        String subject = "비밀번호 변경 인증번호";
        String text = "안녕하세요, " + user.getUserName() + "님!\n\n"
                + "비밀번호 변경을 위한 인증번호는 다음과 같습니다:\n\n"
                + code + "\n\n"
                + "이 인증번호는 5분 동안 유효합니다.";

        sendEmail(email, subject, text);
    }


    // 이메일 전송 로직
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    //  인증번호 검증
    public boolean verifyCode(String email, String code) {
        return code.equals(verificationCodes.get(email));
    }
    
    // 인증번호 삭제 (사용 완료 또는 시간 만료 시)
    public void removeCode(String email) {
        verificationCodes.remove(email);
    }
}
