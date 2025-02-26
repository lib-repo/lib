package org.example.libdev.common;

import lombok.RequiredArgsConstructor;
import org.example.libdev.book.service.BookService;
import org.example.libdev.subject.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final SubjectService subjectService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            model.addAttribute("books", bookService.getAllBooks());

            return "home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());

            return "error";
        }
    }

    @GetMapping("/adminNav")
    public String getAdminNav() {
        return "fragments/adminNav";
    }
}
