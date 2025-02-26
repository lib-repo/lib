package org.example.libdev.book.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.book.service.BookService;
import org.example.libdev.subject.repository.SubjectRepository;
import org.example.libdev.subject.service.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final SubjectService subjectService;

    @GetMapping
    public String getAllBooks(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        try {
            Page<BookResponseDTO> bookPage = bookService.getBooks(page, size);
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("totalBooks", bookPage.getTotalElements());
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("subjects", subjectService.findAll());

            return "book/bookManagement";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{bookId}")
    public String bookDetail(@PathVariable("bookId") Long bookId, Model model) {
        try {

            List<Availability> availabilities =  bookService.checkAvailability(bookId);

            List<Availability> availableLibraries = new ArrayList<>();
            for (Availability availability : availabilities) {
                if (availability.isAvailable()) {
                    availableLibraries.add(availability);
                }
            }

            model.addAttribute("libs", availableLibraries);
            model.addAttribute("book", bookService.getBookById(bookId));
            model.addAttribute("subjects", subjectService.findAll());

            return "book/bookDetail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/subject/{subjectId}")
    public String getBookBySubject(@PathVariable("subjectId") Long subjectId, Model model) {
        try {
            model.addAttribute("books", bookService.getBooksBySubject(subjectId));
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("currentSubject",subjectService.findById(subjectId));

            return "home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam(value = "keyword") String keyword, Model model) {
        try {
            List<BookResponseDTO> books = bookService.searchBooksByTitle(keyword);
            model.addAttribute("books", books);
            model.addAttribute("keyword", keyword);
            model.addAttribute("subjects", subjectService.findAll());

            return "home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
