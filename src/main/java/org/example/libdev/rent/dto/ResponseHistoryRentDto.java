package org.example.libdev.rent.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.libdev.rent.entity.Rent;

@Getter
@Setter
@Builder
public class ResponseHistoryRentDto {
    private Long rentId;
    private String rentDate;
    private String returnDate;
    private String imgUrl;
    private String bookTitle;

    public static ResponseHistoryRentDto toDto(Rent rent){
        return ResponseHistoryRentDto.builder()
                .rentId(rent.getRentId())
                .rentDate(rent.getRentDate())
                .returnDate(rent.getReturnDate())
                .imgUrl(rent.getBook().getImageUrl())
                .bookTitle(rent.getBook().getTitle())
                .build();
    }
}
