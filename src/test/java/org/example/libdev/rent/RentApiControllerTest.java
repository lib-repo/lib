//package org.example.libdev.rent;
//
//import org.example.libdev.book.entity.Book;
//import org.example.libdev.book.repository.BookRepository;
//import org.example.libdev.rent.controller.RentApiController;
//import org.example.libdev.rent.entity.Rent;
//import org.example.libdev.rent.entity.RentStatus;
//import org.example.libdev.rent.entity.User;
//import org.example.libdev.rent.repository.RentRepository;
//import org.example.libdev.rent.repository.UserRepository;
//import org.example.libdev.rent.service.RentService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import java.time.LocalDateTime;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(RentApiController.class)
//@AutoConfigureMockMvc
//public class RentApiControllerTest {
//
//    @Autowired
//    protected MockMvc mvc;
//
//    @MockitoBean
//    private UserRepository userRepository;
//
//    @MockitoBean
//    private BookRepository bookRepository;
//
//    @MockitoBean
//    private RentRepository rentRepository;
//
//    @MockitoBean
//    private RentService rentService;
//
//    @DisplayName("createRent: 대출 생성에 성공한다.")
//    @Test
//    public void createRent() throws Exception {
//        // given
//        User saveUser = createUser("qkralstj@naver.com", "010-1234-5678");
//        Book saveBook = createBook();
//
//        final String url = "/api/rent/" + saveUser.getUserId() + "/" + saveBook.getBookId();
//
//        // when & then
//        mvc.perform(post(url))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("대출 연장에 성공한다.")
//    void updateRentRenew() throws Exception {
//        // given
//        User saveUser = createUser("qkralstj132@naver.com", "010-1234-1324");
//        Book saveBook = createBook();
//        Rent saveRent = createRent(saveUser, saveBook);
//
//        given(rentRepository.save(Mockito.any(Rent.class))).willReturn(saveRent);
//        doNothing().when(rentService).renewRent(Mockito.anyLong());
//
//        final String url = "/api/rent/renew/" + 1;
//
//        mvc.perform(post(url))
//                .andExpect(status().isOk());
//    }
//
//    private User createUser(String email, String phone) {
//        User user = User.builder()
//                .createDate(LocalDateTime.now())
//                .email(email)
//                .password("123456789")
//                .userName("qkralstj")
//                .updateDate(LocalDateTime.now().plusWeeks(2))
//                .phone(phone)
//                .build();
//        return userRepository.save(user);
//    }
//
//    private Book createBook() {
//        Book book = Book.builder()
//                .author("김철수")
//                .available(true)
//                .title("자바프로그래밍")
//                .description("참고")
//                .publicationYear("1997")
//                .publisher("철수출판사")
//                .imageUrl("123456789")
//                .isbn("2131645646")
//                .build();
//        return bookRepository.save(book);
//    }
//
//    private Rent createRent(User user, Book book) {
//        Rent rent = Rent.builder()
//                .rentDate(LocalDateTime.now())
//                .user(user)
//                .status(RentStatus.RENTED)
//                .returnDate(LocalDateTime.now().plusWeeks(2))
//                .book(book)
//                .renew(0)
//                .build();
//        return rentRepository.save(rent);
//    }
//}
