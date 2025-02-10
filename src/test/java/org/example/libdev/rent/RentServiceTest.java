//package org.example.libdev.rent;
//
//import org.example.libdev.rent.entity.Book;
//import org.example.libdev.rent.entity.Rent;
//import org.example.libdev.rent.entity.RentStatus;
//import org.example.libdev.rent.entity.User;
//import org.example.libdev.rent.repository.RentRepository;
//import org.example.libdev.rent.repository.BookRepository;
//import org.example.libdev.rent.repository.UserRepository;
//import org.example.libdev.rent.service.RentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RentServiceTest {
//
//    @Mock
//    private RentRepository rentRepository;
//
//    @Mock
//    private BookRepository bookRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private RentService rentService;
//
//    private Book book;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        book = Book.builder().bookId(1L).title("Test Book").build();
//        user = User.builder().userId(1L).userName("Test User").build();
//    }
//
//    @Test
//    @DisplayName("책과 사용자가 정상적으로 있을 때 대여가 생성된다.")
//    void saveRent_shouldCreateRentSuccessfully() {
//        // given
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        // when
//        rentService.saveRent(1L, 1L);
//
//        // then
//        verify(rentRepository, times(1)).save(any(Rent.class));
//    }
//
//    @Test
//    @DisplayName("책이 없으면 대여 생성 시 예외가 발생한다.")
//    void saveRent_shouldThrowExceptionWhenBookNotFound() {
//        // given
//        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // when
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            rentService.saveRent(1L, 1L);
//        });
//
//        // then
//        assertEquals("책을 찾을 수 없습니다.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("사용자가 없으면 대여 생성 시 예외가 발생한다.")
//    void saveRent_shouldThrowExceptionWhenUserNotFound() {
//        // given
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // whe
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            rentService.saveRent(1L, 1L);
//        });
//
//        // then
//        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
//    }
//}
