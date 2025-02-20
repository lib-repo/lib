package org.example.libdev.book.service;

import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.availabiliy.entity.Availability;
import org.example.libdev.availabiliy.repository.AvailabilityRepository;
import org.example.libdev.book.dto.BookRequestDTO;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.library.entity.Library;
import org.example.libdev.library.repository.LibraryRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final LibraryRepository libraryRepository;
    @Value("${openapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final BookRepository bookRepository;
    private final AvailabilityRepository availabilityRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @PostConstruct
    public void init() {
        saveBooks();
    }

    public void saveBooks() {
        try {
            String url = String.format("http://data4library.kr/api/srchBooks?authKey=%s&pageSize=100&format=json", apiKey);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("API 호출 실패");
            }

            String body = response.getBody();
            log.debug("응답 본문: {}", body);

            JSONObject jsonResponse = new JSONObject(body);

            if (!jsonResponse.has("response") || !jsonResponse.getJSONObject("response").has("docs")) {
                log.error("응답에서 'docs'을 찾을 수 없습니다.");
                throw new RuntimeException("응답에서 'docs'을 찾을 수 없습니다.");
            }

            JSONArray docs = jsonResponse.getJSONObject("response").getJSONArray("docs");

            for (int i = 0; i < docs.length(); i++) {
                JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                Book book = new Book();
                book.setTitle(doc.getString("bookname"));
                book.setAuthor(doc.getString("authors"));
                book.setIsbn(doc.getString("isbn13"));
                book.setPublisher(doc.getString("publisher"));
                book.setPublicationYear(doc.getString("publication_year"));
                book.setImageUrl(doc.getString("bookImageURL"));

                bookRepository.save(book);
            }
        } catch (JSONException e) {
            log.error("JSON 파싱 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            throw new RuntimeException("책 정보를 처리하는 중 오류가 발생했습니다.");
        }
    }

    // 전체 도서 조회
    public List<BookResponseDTO> getAllBooks() {
        try {
            return bookRepository.findAll().stream().map(Book::toResponseDTO).toList();
        } catch (Exception e) {
            log.error("도서 목록을 조회하는 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("도서 목록을 조회하는 중 오류가 발생했습니다.");
        }
    }

    // 상세 도서 조회
    public BookResponseDTO getBookById(Long bookId) {
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new NoSuchElementException("해당 ID의 도서를 찾을 수 없습니다." + bookId));

            log.info("Book ID: {}", book.getBookId());

            return book.toResponseDTO();
        } catch (NoSuchElementException e) {
            log.error("도서 조회 실패: {}", e.getMessage());
            throw new RuntimeException("해당 ID의 도서를 찾을 수 없습니다." + bookId);
        } catch (Exception e) {
            log.error("도서 조회 중 오류가 발생: {}", e.getMessage());
            throw new RuntimeException("도서 조회 중 오류가 발생했습니다.");
        }
    }

    // open api로 도서 정보 조회
    public BookResponseDTO getBookInfoByIsbn(String isbn) {
        try {
            String url = String.format("http://data4library.kr/api/srchDtlList?authKey=%s&isbn13=%s&format=json", apiKey, isbn);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("API 호출 실패");
            }

            String body = response.getBody();
            log.info("book info: {}", body);

            JSONObject jsonResponse = new JSONObject(body);

            if (!jsonResponse.has("response") || !jsonResponse.getJSONObject("response").has("detail")) {
                log.error("응답에서 'detail'을 찾을 수 없습니다.");
                throw new RuntimeException("응답에서 'detail'을 찾을 수 없습니다.");
            }

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
        } catch (JSONException e) {
            log.error("JSON 파싱 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            throw new RuntimeException("책 정보를 처리하는 중 오류가 발생했습니다.");
        }
    }

    // 도서 등록
    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        try {
            Book newBook = bookRequestDTO.toEntity();

            return bookRepository.save(newBook).toResponseDTO();
        } catch (Exception e) {
            log.error("도서 등록 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("도서를 등록하는 중 오류가 발생했습니다.");
        }
    }

    // 도서 수정
    public BookResponseDTO updateBook(Long bookId, BookRequestDTO bookRequestDTO) {
        try {
            Book existingBook = bookRepository.findById(bookId)
                    .orElseThrow(() -> new NoSuchElementException("해당 ID의 도서를 찾을 수 없습니다." + bookId));

            existingBook.setTitle(bookRequestDTO.getTitle());
            existingBook.setAuthor(bookRequestDTO.getAuthor());
            existingBook.setIsbn(bookRequestDTO.getIsbn());
            existingBook.setPublisher(bookRequestDTO.getPublisher());
            existingBook.setPublicationYear(bookRequestDTO.getPublicationYear());
            existingBook.setDescription(bookRequestDTO.getDescription());

            return bookRepository.save(existingBook).toResponseDTO();
        } catch (NoSuchElementException e) {
            log.error("도서 수정 실패: {}", e.getMessage());
            throw new RuntimeException("해당 ID의 도서를 찾을 수 업습니다." + bookId);
        } catch (Exception e) {
            log.error("도서 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("도서를 수정하는 중 오류가 발생했습니다.");
        }
    }

    // 도서 삭제
    public void deleteBookById(Long bookId) {
        try {
            Book existingBook = bookRepository.findById(bookId)
                    .orElseThrow(() -> new NoSuchElementException("해당 ID의 도서를 찾을 수 없습니다." + bookId));

            bookRepository.deleteById(bookId);
        } catch (NoSuchElementException e) {
            log.error("도서 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("해당 ID의 도서를 찾을 수 업습니다." + bookId);
        } catch (Exception e) {
            log.error("도서 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("도서를 삭제하는 중 오류가 발생했습니다.");
        }
    }

    // 도서 대여 가능 여부 확인
//    @Cacheable(value = "availability", key = "#bookId", unless = "#result == null", cacheManager = "cacheManager")
    public List<Availability> checkAvailability(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 도서를 찾을 수 없습니다." + bookId));

        List<Availability> availabilityList = availabilityRepository.findByBook(book);

        if (availabilityList.isEmpty()) {
            List<Library> libraries = libraryRepository.findAll();

            for (Library library : libraries) {
                Availability availability = new Availability();
                availability.setBook(book);
                availability.setLibrary(library);
                availability.setAvailable(false);

                availabilityRepository.save(availability);
            }

            availabilityList = availabilityRepository.findByBook(book);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Availability availability : availabilityList) {
            if (!availability.isAvailable()) {
                futures.add(checkAvailabilityAsync(availability, book));
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return availabilityList;
    }

    // api 호출을 비동기로 처리
    public CompletableFuture<Void> checkAvailabilityAsync(Availability availability, Book book) {
        String url = String.format("http://data4library.kr/api/bookExist?authKey=%s&libCode=%s&isbn13=%s&format=json", apiKey, availability.getLibrary().getLibraryCode(), book.getIsbn());
        log.info("url: {}", url);

        return CompletableFuture.runAsync(() -> {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                String body = response.getBody();
                log.info("book availability: {}", body);

                JSONObject jsonResponse = new JSONObject(body);

                if (!jsonResponse.has("response") || !jsonResponse.getJSONObject("response").has("result")) {
                    log.error("응답에서 'result'를 찾을 수 없습니다.");
                    throw new RuntimeException("응답에서 'result'를 찾을 수 없습니다.");
                }

                JSONObject result = jsonResponse.getJSONObject("response").getJSONObject("result");

                if (result.getString("loanAvailable").equals("Y")) {
                    availability.setAvailable(true);
                } else {
                    availability.setAvailable(false);
                }

                availabilityRepository.save(availability);
            } catch (JSONException e) {
                log.error("JSON 파싱 중 오류 발생: {}",e.getMessage());
                throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다.");
            } catch (Exception e) {
                log.error("예외 발생: {}", e.getMessage());
                throw new RuntimeException("책 정보를 처리하는 중 오류가 발생했습니다.");
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}