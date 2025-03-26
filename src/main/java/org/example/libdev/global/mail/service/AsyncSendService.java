package org.example.libdev.global.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.global.exception.NotFoundException;
import org.example.libdev.rent.entity.EmailMessage;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.example.libdev.rent.repository.RentRepository;
import org.example.libdev.user.entity.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
@RequiredArgsConstructor
public class AsyncSendService {

    private final RentRepository rentRepository;
    private final JavaMailSender javaMailSender;

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

        sendMail(emailMessage);
    }

    /**
     *  결제 완료 메일 전송
     */
    @Transactional
    public void sendPaymentConfirmationMail(Long rentId) {
        Rent rent = rentRepository.findById(rentId).orElseThrow(
                () -> new IllegalArgumentException("Rent 내역을 찾을 수 없습니다.")
        );

        User user = rent.getUser();
        Book book = rent.getBook();
        String subject = "결제 완료 알림: " + book.getTitle();
        String message = "안녕하세요, " + user.getUserName() + "님.\n\n"
                + "책 \"" + book.getTitle() + "\"의 대출 결제가 성공적으로 완료되었습니다.\n"
                + "책은 대출 기한 내에 반납해 주시길 바랍니다.\n\n"
                + "이용해 주셔서 감사합니다.";

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject(subject)
                .message(message)
                .build();

        sendMail(emailMessage);
    }


    /**
     * 도착 알림 메일 보내기
     */
    @Transactional
    public void sendArrivalNotification(Long rentId) {
        Rent rent = rentRepository.findById(rentId).orElseThrow(
                () ->  new NotFoundException.RentNotFoundException("Rent")
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

        sendMail(emailMessage);
    }

    @Async("mailExecutor")
    public CompletableFuture<String> sendMail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), true);
            javaMailSender.send(mimeMessage);

            return CompletableFuture.completedFuture("메일 전송 성공");

        } catch (MessagingException e) {
            return CompletableFuture.failedFuture(new RuntimeException("메일 전송 실패", e));
        }
    }
}
