package org.example.libdev.rent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.global.entity.BaseEntity;
import org.example.libdev.rent.dto.ResponseRentDto;

import java.awt.print.Book;
import java.time.LocalDateTime;

@Entity
@Table(name = "rents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Rent {

    @Id
    @GeneratedValue
    private Long rentId;

    @Enumerated(EnumType.STRING)
    private RentStatus status;

    @Column(nullable = false)
    private LocalDateTime rentDate;

    @Column(nullable = false)
    private LocalDateTime returnDate;

    @Column(columnDefinition = "TINYINT CHECK (renew <=1)")     // 연장 횟수를 1회로 제한
    private int renew;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    public void updateRenew(int renew, LocalDateTime returnDate) {
        this.renew = renew;
        this.returnDate = returnDate;
    }

}
