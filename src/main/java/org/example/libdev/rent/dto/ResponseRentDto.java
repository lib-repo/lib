package org.example.libdev.rent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRentDto {


    private RentStatus status;
    private LocalDateTime rentDate;
    private LocalDateTime returnDate;
    private int renew;
    private Book book;
    private Long rentId;

    public static ResponseRentDto toResponseRentDto(Rent rent) {
        return ResponseRentDto.builder()
                .rentId(rent.getRentId())
                .rentDate(rent.getRentDate())
                .returnDate(rent.getReturnDate())
                .renew(rent.getRenew())
                .book(rent.getBook())
                .status(rent.getStatus())
                .build();
    }
}
