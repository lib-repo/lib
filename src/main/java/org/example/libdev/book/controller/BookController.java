package org.example.libdev.book.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.book.service.BookService;
import org.example.libdev.subject.repository.SubjectRepository;
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
    private final SubjectRepository subjectRepository;

    @GetMapping
    public String getAllBooks(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        try {
            Page<BookResponseDTO> bookPage = bookService.getBooks(page, size);
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("totalBooks", bookPage.getTotalElements());
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("subjects", subjectRepository.findAll());

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

            return "book/bookDetail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
