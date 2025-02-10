package org.example.libdev.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.book.dto.BookRequestDTO;
import org.example.libdev.book.dto.BookResponseDTO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    @Value("${openapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final BookRepository bookRepository;

    // 전체 도서 조회
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(Book::toResponseDTO).toList();
    }

    // 상세 도서 조회
    public BookResponseDTO getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();

        log.info("Book ID: {}", book.getBookId());

        return book.toResponseDTO();
    }

    // open api로 도서 정보 조회
    public BookResponseDTO getBookInfoByIsbn(String isbn) {
        String url = String.format("http://data4library.kr/api/srchDtlList?authKey=%s&isbn13=%s&format=json", apiKey, isbn);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String body = response.getBody();
        log.info("book info: {}", body);

        JSONObject jsonResponse = new JSONObject(body);
        JSONObject bookDetails = jsonResponse.getJSONObject("response").getJSONArray("detail").getJSONObject(0).getJSONObject("book");

        return BookResponseDTO.builder()
                .title(bookDetails.getString("bookname"))
                .author(bookDetails.getString("authors"))
                .isbn(bookDetails.getString("isbn13"))
                .publisher(bookDetails.getString("publisher"))
                .publicationYear(bookDetails.getString("publication_year"))
                .imageUrl(bookDetails.getString("bookImageURL"))
                .description((bookDetails.getString("description")))
                .build();
    }

    // 도서 등록
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        Book newBook = bookRequestDTO.toEntity();

        return bookRepository.save(newBook).toResponseDTO();
    }

    // 도서 수정
    public BookResponseDTO updateBook(Long bookId, BookRequestDTO bookRequestDTO) {
        Book existingBook = bookRepository.findById(bookId).orElseThrow();

        existingBook.setTitle(bookRequestDTO.getTitle());
        existingBook.setAuthor(bookRequestDTO.getAuthor());
        existingBook.setIsbn(bookRequestDTO.getIsbn());
        existingBook.setPublisher(bookRequestDTO.getPublisher());
        existingBook.setPublicationYear(bookRequestDTO.getPublicationYear());
        existingBook.setDescription(bookRequestDTO.getDescription());

        return bookRepository.save(existingBook).toResponseDTO();
    }

    // 도서 삭제
    public void deleteBookById(Long bookId) {
        Book existingBook = bookRepository.findById(bookId).orElseThrow();

        bookRepository.deleteById(bookId);
    }
}
