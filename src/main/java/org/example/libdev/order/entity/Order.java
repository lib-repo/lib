package org.example.libdev.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.library.entity.LibraryAgreement;
import org.example.libdev.payment.entity.PaymentEntity;
import org.example.libdev.user.entity.User;

@Entity
@Table(name = "Orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "agreement_id",nullable = false)
    private LibraryAgreement agreement;

    @OneToOne
    private PaymentEntity payment;

}
