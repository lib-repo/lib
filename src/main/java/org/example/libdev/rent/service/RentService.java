package org.example.libdev.rent.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.availability.repository.AvailabilityRepository;
import org.example.libdev.availability.service.AvailabilityService;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.dto.ResponseHistoryRentDto;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentService {
    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityService availabilityService;

    /**
     * rent 생성
     */
    @Transactional
    public void saveRent(Long userId, Long bookId, Long libraryId, Long availabilityId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("책을 찾을 수 없습니다.")
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        Availability availability = availabilityService.getAvailability(availabilityId);

        if(!availability.isAvailable()){
            throw new IllegalStateException("이미 대출되었습니다.");
        }else{
            availability.setAvailable(false);
            availabilityRepository.save(availability);
        }


        LocalDate rentDate = LocalDate.now();
        LocalDate returnDate = rentDate.plusWeeks(2);

        Rent rent = Rent.builder()
                .rentDate(rentDate.toString())
                .status(RentStatus.RENTED)
                .returnDate(returnDate.toString())
                .libraryId(libraryId)
                .book(book)
                .user(user)
                .renew(0)
                .build();

        rentRepository.save(rent);
    }

    /**
     * 대여 현황 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseRentDto> selectRentByUserId(Long userId, String status) {
        List<Rent> rentsByUser;

        if (status.isEmpty() || status.equalsIgnoreCase("ALL")) {
            rentsByUser = rentRepository.findByUser_UserIdxAndStatusNot(userId, RentStatus.RETURNED)
                    .orElseThrow(()-> new IllegalStateException("사용자의 대여 내역을 찾을 수 없습니다."));
        } else {
            RentStatus rentStatus = RentStatus.valueOf(status.toUpperCase());
            rentsByUser = rentRepository.findByUser_UserIdxAndStatus(userId, rentStatus).orElseThrow(
                    () -> new IllegalStateException("사용자의 대여 내역을 찾을 수 없습니다.")
            );
        }

        return rentsByUser.stream()
                .sorted(Comparator.comparing(Rent::getRentDate).reversed())
                .map(ResponseRentDto::toResponseRentDto)
                .toList();
    }


    /**
     *  대여 내역 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseHistoryRentDto> historyRentByUser(Long userId){

        List<Rent> historyRents = rentRepository.findByUser_UserIdxAndStatus(userId,RentStatus.RETURNED).orElseThrow(
                () -> new IllegalStateException("대여 내역을 찾을 수 없습니다.")
        );

        return historyRents.stream()
                .sorted(Comparator.comparing(Rent::getRentDate).reversed())
                .map(ResponseHistoryRentDto::toDto)
                .toList();
    }

    /**
     * 대여 연장
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
     *  대여 내역 조회(관리자)
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
    public void returnRent(Long rentId) {
        Rent returnRent = rentRepository.findById(rentId).orElseThrow(
                () -> new IllegalArgumentException("Rent 내역을 찾을 수 없습니다.")
        );

        Book returnedBook = returnRent.getBook();
        Long rentedLibraryId = returnRent.getLibraryId();

        Availability targetAvailability = returnedBook.getBookAvailabilities().stream()
                .filter(av -> av.getLibrary().getLibraryId().equals(rentedLibraryId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 도서관의 Availability 정보를 찾을 수 없습니다."));

        targetAvailability.setAvailable(true);
//        targetAvailability.setUpdateDate();

        LocalDate returnDate = LocalDate.now();
        returnRent.updateReturnStatusAndDate(RentStatus.RETURNED, returnDate.toString());

        bookRepository.save(returnedBook);
        rentRepository.save(returnRent);
    }

    /**
     *  연체 확인 스케줄러
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateOverdueStatus(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Rent> overdueRents = rentRepository.findByStatus(RentStatus.RENTED).orElseThrow(
                () -> new IllegalStateException("연체된 내역이 없습니다.")
        );

        for(Rent rent : overdueRents){
            LocalDate returnDate = LocalDate.parse(rent.getReturnDate(), formatter);
            if(returnDate.isBefore(today)){
                rent.updateRentStatus(RentStatus.OVERDUE);
                sendOverdueMail(rent.getRentId());
            }
        }

        rentRepository.saveAll(overdueRents);

    }

    /**
     *  연체 메일 전송
     */
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

        sendMail(emailMessage, "arrival");
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
