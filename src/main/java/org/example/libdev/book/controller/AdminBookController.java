package org.example.libdev.book.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.book.dto.BookRequestDTO;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.book.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookInfoByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookInfoByIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookRequestDTO));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long bookId, @RequestBody BookRequestDTO bookRequestDTO) {
        return ResponseEntity.ok(bookService.updateBook(bookId, bookRequestDTO));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long bookId) {
        bookService.deleteBookById(bookId);
        return ResponseEntity.noContent().build();
    }
}
