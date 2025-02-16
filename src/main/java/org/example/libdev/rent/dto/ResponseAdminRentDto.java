package org.example.libdev.rent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libdev.book.entity.Book;
import org.example.libdev.rent.entity.RentStatus;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdminRentDto {
    private RentStatus status;
    private String rentDate;
    private String returnDate;
    private int renew;
    private Book book;
    private Long rentId;
}
