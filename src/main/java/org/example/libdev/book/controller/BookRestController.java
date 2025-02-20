package org.example.libdev.book.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.book.dto.BookRequestDTO;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.book.service.BookService;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        try {
            return ResponseEntity.ok(bookService.getAllBooks());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long bookId) {
        try {
            return ResponseEntity.ok(bookService.getBookById(bookId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookInfoByIsbn(@PathVariable String isbn) {
        try {
            BookResponseDTO bookResponseDTO = bookService.getBookInfoByIsbn(isbn);
            return ResponseEntity.ok(bookResponseDTO);
        } catch (JSONException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO bookRequestDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookRequestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long bookId, @RequestBody BookRequestDTO bookRequestDTO) {
        try {
            return ResponseEntity.ok(bookService.updateBook(bookId, bookRequestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity deleteBookById(@PathVariable Long bookId) {
        try {
            bookService.deleteBookById(bookId);
            return ResponseEntity.ok("삭제가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
