package org.example.libdev.user.controller;

import org.example.libdev.user.dto.UserRequestDto;
import org.example.libdev.user.dto.UserLoginDto;
import org.example.libdev.user.service.AuthCodeService;
import org.example.libdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthCodeService authCodeService;
    @GetMapping("/register")
    public String registerPage() {
        return "User/register";
    }

    // 회원가입
    @PostMapping("/register")
    public String register(@ModelAttribute UserRequestDto requestDto) {
        userService.register(requestDto);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "User/login";
    }

    // 로그인
    @PostMapping("/login")
    public String login(@ModelAttribute UserLoginDto requestDto, Model model) {
        String token = userService.login(requestDto);
        model.addAttribute("token", token);
        return "home";
    }

    @GetMapping("/find-id")
    public String findIdPage() {
        return "User/find-id";
    }

    @PostMapping("/find-id")
    public String findUserId(@RequestParam String userName, @RequestParam String email, Model model) {
        String userId = userService.findUserIdByNameAndEmail(userName, email);
        model.addAttribute("userId", userId);
        model.addAttribute("message", "회원님의 아이디는 아래와 같습니다.");
        return "User/find-id";
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "User/find-password";
    }

    // 인증번호 이메일 전송 
    @PostMapping("/find-password/send-code")
    public String sendVerificationCode(
            @RequestParam String userId,
            @RequestParam String email,
            Model model) {

        authCodeService.sendVerificationCode(userId, email);
        model.addAttribute("email", email);
        model.addAttribute("userId", userId);
        model.addAttribute("message", "이메일로 인증번호가 전송되었습니다.");
        model.addAttribute("step", "verify");
        return "User/find-password";
    }

    // 인증번호 확인 후 비밀번호 변경
    @PostMapping("/find-password/reset")
    public String resetPassword(
            @RequestParam String userId,
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword,
            Model model) {

        userService.resetPasswordWithCode(userId, email, code, newPassword);
        return "redirect:/user/login";
    }
}

