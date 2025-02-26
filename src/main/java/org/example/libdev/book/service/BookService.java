package org.example.libdev.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.availability.dto.AvailabilityDTO;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.availability.repository.AvailabilityRepository;
import org.example.libdev.book.dto.BookRequestDTO;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.example.libdev.library.entity.Library;
import org.example.libdev.library.repository.LibraryRepository;
import org.example.libdev.subject.entity.Subject;
import org.example.libdev.subject.repository.SubjectRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final LibraryRepository libraryRepository;
    private final SubjectRepository subjectRepository;
    private final BookRepository bookRepository;
    private final AvailabilityRepository availabilityRepository;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, AvailabilityDTO> redisTemplate;

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Value("${openapi.key}")
    private String apiKey;

    // 전체 도서 조회
    public List<BookResponseDTO> getAllBooks() {
        try {
            return bookRepository.findAll().stream().map(Book::toResponseDTO).toList();
        } catch (Exception e) {
            log.error("도서 목록을 조회하는 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("도서 목록을 조회하는 중 오류가 발생했습니다.");
        }
    }

    // 전체 도서 목록 (페이지네이션)
    public Page<BookResponseDTO> getBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable).map(Book::toResponseDTO);
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
            Subject subject = subjectRepository.findById(bookRequestDTO.getSubjectId())
                    .orElseThrow(() -> new NoSuchElementException("해당 ID의 주제를 찾을 수 없습니다."));

            newBook.setSubject(subject);

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

            Subject subject = subjectRepository.findById(bookRequestDTO.getSubjectId())
                    .orElseThrow(() -> new NoSuchElementException("해당 ID의 주제를 찾을 수 없습니다."));

            existingBook.setSubject(subject);
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
    public List<Availability> checkAvailability(Long bookId) {
        List<Availability> availableLibraries = getAvailabilityFromCache(bookId);

        if (!availableLibraries.isEmpty()) {
            return availableLibraries;
        }

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
        saveAvailabilityToCache(bookId, availabilityList);

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
                log.error("JSON 파싱 중 오류 발생: {}", e.getMessage());
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

    // Redis 캐시 조회
    public List<Availability> getAvailabilityFromCache(Long bookId) {
        String cacheKey = "availability:" + bookId;
        List<AvailabilityDTO> availabilityDTOList = redisTemplate.opsForList().range(cacheKey, 0, -1);

        if (availabilityDTOList.isEmpty() || availabilityDTOList == null) {
            return Collections.emptyList();
        }

        List<Availability> availabilityList = new ArrayList<>();

        for (AvailabilityDTO availabilityDTO : availabilityDTOList) {
            Availability availability = new Availability();
            availability.setBook(bookRepository.findById(availabilityDTO.getBookId()).orElse(null));
            availability.setLibrary(libraryRepository.findById(availabilityDTO.getLibraryId()).orElse(null));
            availability.setAvailable(availabilityDTO.isAvailable());
            availability.setAvailabilityId(availabilityDTO.getAvailabilityId());
            availabilityList.add(availability);
        }

        return availabilityList;
    }

    // 캐시 저장
    public void saveAvailabilityToCache(Long bookId, List<Availability> availabilityList) {
        String cacheKey = "availability:" + bookId;

        for (Availability availability : availabilityList) {
            AvailabilityDTO availabilityDTO = new AvailabilityDTO();
            availabilityDTO.setBookId(availability.getBook().getBookId());
            availabilityDTO.setLibraryId(availability.getLibrary().getLibraryId());
            availabilityDTO.setAvailable(availability.isAvailable());
            availabilityDTO.setAvailabilityId(availability.getAvailabilityId());

            redisTemplate.opsForList().rightPush(cacheKey, availabilityDTO);
        }
    }

    // 캐시 초기화
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearRedisCache() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        log.info("clear redis cache");
    }
}