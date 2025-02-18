package org.example.libdev.rent.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.dto.ResponseRentDto;
import org.example.libdev.rent.entity.EmailMessage;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.example.libdev.rent.repository.RentRepository;
import org.example.libdev.user.entity.User;
import org.example.libdev.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentService {
    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

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
    public List<ResponseRentDto> selectRentByUserId(Long userId) {
        List<Rent> rentsByUser = rentRepository.findByUser_UserIdxAndStatus(userId, RentStatus.RENTED);
        return rentsByUser.stream()
                .map(ResponseRentDto::toResponseRentDto)
                .toList();
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

    /**
     *  관리자 rent 내역 조회
     */
    @Transactional(readOnly = true)
    public Page<ResponseAdminRentDto> selectAdminRentByUserId(String bookTitle, Pageable pageable){
        Page<Rent> rents = rentRepository.findByBookTitleContaining(bookTitle,pageable);

        return rents.map(ResponseAdminRentDto::toDto);
    }

    /**
     *  rent 반납 기능
     */

    @Transactional
    public void returnRent(Long rentId){

        Rent returnRent = rentRepository.findById(rentId).orElseThrow(
                () -> new IllegalArgumentException("Rent 내역을 찾을 수 없습니다.")
        );

        Book availableBook = returnRent.getBook();

        availableBook.updateAvailable(true);
        LocalDate returnDate = LocalDate.now();
        returnRent.updateReturnStatusAndDate(RentStatus.RETURNED, returnDate.toString());

        bookRepository.save(availableBook);
        rentRepository.save(returnRent);
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
                sendOverdueMail(rent.getRentId());
            }
        }

        rentRepository.saveAll(overdueRents);

    }

    @Transactional
    public void sendOverdueMail(Long rentId) {
        Rent rent = rentRepository.findById(rentId).orElseThrow(
                () -> new IllegalArgumentException("Rent 내역을 찾을 수 없습니다.")
        );

        rent.updateRentStatus(RentStatus.OVERDUE);
        rentRepository.save(rent);

        User user = rent.getUser();
        Book book = rent.getBook();
        String subject = "책 연체 알림: " + book.getTitle();
        String message = "안녕하세요, " + user.getUserName() + "님.\n\n"
                + "대출하신 책 \"" + book.getTitle() + "\"의 반납 기한이 지났습니다.\n"
                + "연체된 책은 반환되기 전까지 연체료가 부과될 수 있습니다.\n\n"
                + "반납을 서두르시길 바랍니다.";

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject(subject)
                .message(message)
                .build();

        sendMail(emailMessage, "overdue");
    }

    /**
     * 도착 알림 메일 보내기
     */
    @Transactional
    public void sendArrivalNotification(Long rentId) {
        Rent rent = rentRepository.findById(rentId).orElseThrow(
                () -> new IllegalArgumentException("Rent 내역을 찾을 수 없습니다.")
        );

        User user = rent.getUser();
        Book book = rent.getBook();
        String subject = "도착 알림: " + book.getTitle();
        String message = "안녕하세요, " + user.getUserName() + "님.\n\n"
                + "대출하신 책 \"" + book.getTitle() + "\"이 도착했습니다.\n"
                + "반납 기한 내에 반납해주세요.\n\n"
                + "감사합니다.";

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject(subject)
                .message(message)
                .build();

        log.info("이메일:{}" ,user.getEmail());
        sendMail(emailMessage, "arrival");
        log.info("성공 여부:{}", sendMail(emailMessage, "arrival"));
    }


    public String sendMail(EmailMessage emailMessage, String type) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true);
            javaMailSender.send(mimeMessage);

            return "메일 전송 성공";

        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }

}
