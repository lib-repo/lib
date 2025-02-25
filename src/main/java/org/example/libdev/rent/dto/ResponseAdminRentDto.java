package org.example.libdev.rent.dto;

import lombok.*;
import org.example.libdev.book.entity.Book;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.example.libdev.user.entity.User;


@Builder
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminRentDto {
    private RentStatus status;
    private String rentDate;
    private String returnDate;
    private int renew;
    private Book book;
    private Long rentId;
    private User user;

    public static ResponseAdminRentDto toDto(Rent rent) {
        return ResponseAdminRentDto.builder()
                .status(rent.getStatus())
                .rentDate(rent.getRentDate())
                .returnDate(rent.getReturnDate())
                .renew(rent.getRenew())
                .book(rent.getBook())
                .user(rent.getUser())
                .rentId(rent.getRentId())
                .build();
    }
}
