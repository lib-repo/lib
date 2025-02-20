package org.example.libdev.book.controller;


import lombok.RequiredArgsConstructor;
import org.example.libdev.availabiliy.entity.Availability;
import org.example.libdev.book.service.BookService;
import org.example.libdev.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String getAllBooks(Model model) {
        try {
            model.addAttribute("books", bookService.getAllBooks());

            return "bookManagement";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{bookId}")
    public String bookDetail(@PathVariable("bookId") Long bookId, Model model) {
        try {

            List<Availability>  Availabilities =  bookService.checkAvailability(bookId);

            List<Availability> availableLibraries = new ArrayList<>();
            for (Availability availability : Availabilities) {
                if (availability.isAvailable()) {
                    availableLibraries.add(availability);
                }
            }

            model.addAttribute("libs", availableLibraries);
            model.addAttribute("book", bookService.getBookById(bookId));

            return "bookDetail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
