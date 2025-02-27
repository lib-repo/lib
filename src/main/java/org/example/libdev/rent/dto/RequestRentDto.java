package org.example.libdev.rent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestRentDto {
    private Long userId;
    private Long libraryId;
    private Long availabilityId;
}
