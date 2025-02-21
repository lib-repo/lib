package org.example.libdev.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/adminMain")
    public String home() {
        return "adminMain";
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "userMyPage";
    }
}
