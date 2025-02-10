//package org.example.libdev.rent;
//
//import org.example.libdev.rent.entity.Book;
//import org.example.libdev.rent.entity.Library;
//import org.example.libdev.rent.entity.User;
//import org.example.libdev.rent.repository.BookRepository;
//import org.example.libdev.rent.repository.LibraryRepository;
//import org.example.libdev.rent.repository.UserRepository;
//import org.example.libdev.subject.entity.Subject;
//import org.example.libdev.subject.repository.SubjectRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import java.time.LocalDateTime;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class RentApiControllerTest {
//
//    @Autowired
//    protected MockMvc mvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @Autowired
//    private LibraryRepository libraryRepository;
//
//    @Autowired
//    private SubjectRepository subjectRepository;
//
//    @DisplayName("createRent: 대출 생성에 성공한다.")
//    @Test
//    public void createRent() throws Exception {
//
//        // given
//        Library saveLibrary = Library.builder()
//                .libraryLocation("인천")
//                .libraryName("인천도서관")
//                .libraryPhone("010-1234-5678")
//                .build();
//
//        libraryRepository.save(saveLibrary);
//
//        Subject saveSubject = new Subject("IT");
//
//        subjectRepository.save(saveSubject);
//
//        User saveUser = User.builder()
//                .library(saveLibrary)
//                .createDate(LocalDateTime.now())
//                .email("qkralstj@naver.com")
//                .password("123456789")
//                .userName("qkralstj")
//                .updateDate(LocalDateTime.now().plusWeeks(2))
//                .phone("010-1234-5678")
//                .build();
//
//        userRepository.save(saveUser);
//
//        Book saveBook = Book.builder()
//                .library(saveLibrary)
//                .createDate(LocalDateTime.now())
//                .author("김철수")
//                .available(true)
//                .title("자바프로그래밍")
//                .bookImageUrl("ASDFASFASFD")
//                .isbn(123456789L)
//                .description("참고")
//                .publicationYear("1997")
//                .publisher("철수출판사")
//                .subject(saveSubject)
//                .updateDate(LocalDateTime.now().plusWeeks(2))
//                .build();
//
//        bookRepository.save(saveBook);
//
//        final String url = "/api/rent/rent/" + saveUser.getUserId() + "/" + saveBook.getBookId();
//
//        // when & then
//        mvc.perform(post(url))
//                .andExpect(status().isOk());
//    }
//}
