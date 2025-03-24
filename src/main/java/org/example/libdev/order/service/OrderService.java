package org.example.libdev.order.service;

import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.book.entity.Book;
import org.example.libdev.book.repository.BookRepository;
import org.example.libdev.global.exception.NotFoundException;
import org.example.libdev.library.entity.LibraryAgreement;
import org.example.libdev.library.repository.LibraryAgreementRepository;
import org.example.libdev.order.dto.RequestOrderDto;
import org.example.libdev.order.entity.Order;
import org.example.libdev.order.entity.OrderStatus;
import org.example.libdev.order.repository.OrderRepository;
import org.example.libdev.payment.dto.RequestPaymentDto;
import org.example.libdev.payment.entity.PaymentEntity;
import org.example.libdev.payment.service.PaymentService;
import org.example.libdev.user.entity.User;
import org.example.libdev.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LibraryAgreementRepository libraryAgreementRepository;

    public void saveOrder(RequestOrderDto orderDto) throws IamportResponseException, IOException {

        log.info("tlqkf:{}", orderDto.getMerchantUid());
        RequestPaymentDto requestPaymentDto = RequestPaymentDto
                .builder()
                .merchantUid(orderDto.getMerchantUid())
                .amount(orderDto.getAmount())
                .orderName(orderDto.getOrderName())
                .bookName(orderDto.getBookName())
                .orderEmail(orderDto.getOrderEmail())
                .build();

        PaymentEntity orderPayment = paymentService.postPrepare(requestPaymentDto);

        Book orderBook = bookRepository.findById(orderDto.getBookId()).orElseThrow(
                () ->  new NotFoundException.BookNotFoundException("Book")
        );

        User orderUser = userRepository.findByUserId(orderDto.getUserId()).orElseThrow(
                () -> new NotFoundException.UserNotFoundException("User")
        );

        LibraryAgreement orderLibrary = libraryAgreementRepository.findLibraryAgreementByFromLibrary_LibraryIdAndToLibrary_LibraryId(
                orderDto.getSelectedLibraryId(),orderDto.getToLibraryId()
        ).orElseThrow(
                () -> new NotFoundException.LibraryAgreementNotFoundException("Library Agreement")
        );

        Order order = Order.builder()
                .user(orderUser)
                .book(orderBook)
                .agreement(orderLibrary)
                .orderDate(LocalDate.now().toString())
                .orderStatus(OrderStatus.COMPLETED)
                .payment(orderPayment)
                .build();

        orderRepository.save(order);
    }
}
