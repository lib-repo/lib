package org.example.libdev.rent.service;

import lombok.RequiredArgsConstructor;
import org.example.libdev.rent.entity.Book;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.example.libdev.rent.entity.User;
import org.example.libdev.rent.repository.BookRepository;
import org.example.libdev.rent.repository.RentRepository;
import org.example.libdev.rent.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository rentRepository;
//    private final BookRepository bookRepository;
//    private final UserRepository userRepository;

    /**
     * rent 생성
     */
//    @Transactional
//    public void saveRent(Long userId, Long bookId){
//
//        Book book = bookRepository.findById(bookId).orElseThrow(
//                ()->new IllegalStateException("책을 찾을 수 없습니다.")
//        );
//
//        User user = userRepository.findById(userId).orElseThrow(
//                ()->new IllegalStateException("사용자를 찾을 수 없습니다.")
//        );
//
//        Rent rent = Rent.builder()
//                .rentDate(LocalDateTime.now())
//                .status(RentStatus.RENTED)
//                .returnDate(LocalDateTime.now().plusWeeks(2))
//                .book(book)
//                .user(user)
//                .renew(0)
//                .build();
//
//        rentRepository.save(rent);
//    }


}
