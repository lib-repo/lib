package org.example.libdev.rent.service;

import lombok.RequiredArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.example.libdev.rent.dto.ResponseRentDto;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.example.libdev.rent.entity.User;
import org.example.libdev.rent.repository.RentRepository;
import org.example.libdev.rent.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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
//        if (!book.getAvailable()) {
//            throw new IllegalStateException("책이 대출 가능한 상태가 아닙니다.");
//        }
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

    /**
     * rent 조회
     */

    @Transactional(readOnly = true)
    public List<ResponseRentDto> selectRentByUserId(Long userId){

        List<Rent> rentsByUser = rentRepository.findByUser_UserId(userId);

        List<ResponseRentDto> responseList = rentsByUser.stream()
                .map(ResponseRentDto::toResponseRentDto)
                .collect(Collectors.toList());

        return responseList;
    }

    /**
     * rent 연장
     */
    @Transactional
    public void renewRent(Long rentId){

        Rent renewRent = rentRepository.findById(rentId).orElseThrow(
                () -> new IllegalStateException("대출 내역을 찾을 수 없습니다.")
        );

        if(!renewRent.getStatus().equals(RentStatus.RENTED)){
            throw new IllegalStateException("대출 상태에만 연장할 수 있습니다.");
        }

        if(renewRent.getRenew()>=1){
            throw new IllegalStateException("연장 횟수를 초과했습니다.");
        }

        renewRent.updateRenew(renewRent.getRenew() +1, renewRent.getReturnDate().plusDays(7));
        rentRepository.save(renewRent);
    }
}
