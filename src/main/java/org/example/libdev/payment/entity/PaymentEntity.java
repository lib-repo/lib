package org.example.libdev.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.libdev.order.entity.Order;

@Getter
@Entity
@Table(name = "Payments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private String impUid;

    private String pg_uid;

    private String payment_method;

    private String apply_num;

    private String bank_code;
    private String bank_name;
    private String card_code;
    private String card_number;
    private int card_quota;
    private String book_name;
    @Column(nullable = false)
    private int amount;
    private String order_name;
    private String order_email;
    private String order_phone;
    private String payment_status;
    private String payment_time;
    private String fail_time;
    private String fail_reason;
    private String receipt_url;
    private String cash_receipt_issued;

    @Column(nullable = false)
    private String pay_create_at;
    private String pay_update_at;

    @OneToOne
    private Order order;
}
