package org.example.libdev.rent.service;

import lombok.RequiredArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.dto.ResponseRentDto;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.example.libdev.rent.entity.User;
import org.example.libdev.rent.repository.RentRepository;
import org.example.libdev.rent.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * rent 생성
     */
    @Transactional
    public void saveRent(Long userId, Long bookId){

        Book book = bookRepository.findById(bookId).orElseThrow(
                ()->new IllegalStateException("책을 찾을 수 없습니다.")
        );

        if (!book.getAvailable()) {
            throw new IllegalStateException("책이 대출 가능한 상태가 아닙니다.");
        }

        User user = userRepository.findById(userId).orElseThrow(
                ()->new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime nowDateTime = LocalDateTime.now();
        String formattedDate = formatter.format(nowDateTime);
        String returnFormattedDate = formatter.format(nowDateTime.plusWeeks(2));

        Rent rent = Rent.builder()
                .rentDate(formattedDate)
                .status(RentStatus.RENTED)
                .returnDate(returnFormattedDate)
                .book(book)
                .user(user)
                .renew(0)
                .build();

        rentRepository.save(rent);
    }

    /**
     * rent 조회
     */

    @Transactional(readOnly = true)
    public Page<ResponseRentDto> selectRentByUserId(Long userId, Pageable pageable){

        Page<Rent> rentsByUser = rentRepository.findByUser_UserId(userId,pageable);

        return rentsByUser.map(ResponseRentDto::toResponseRentDto);
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

        LocalDate currentReturnDate = LocalDate.parse(renewRent.getReturnDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        LocalDate newReturnDate = currentReturnDate.plusDays(7);

        renewRent.updateRenew(renewRent.getRenew() + 1, newReturnDate);
        rentRepository.save(renewRent);
    }

    @Transactional(readOnly = true)
    public Page<ResponseAdminRentDto> selectAdminRentByUserId(Pageable pageable){
        Page<Rent> rents = rentRepository.findAll(pageable);

        return rents.map(ResponseAdminRentDto::toDto);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateOverdueStatus(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Rent> overdueRents = rentRepository.findByStatus(RentStatus.RENTED);

        for(Rent rent : overdueRents){
            LocalDate returnDate = LocalDate.parse(rent.getReturnDate(), formatter);
            if(returnDate.isBefore(today)){
                rent.updateRentStatus(RentStatus.OVERDUE);
            }
        }

        rentRepository.saveAll(overdueRents);

    }
}
