package org.example.libdev.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityDTO {

    private Long availabilityId;
    private boolean available;
    private Long bookId;
    private Long libraryId;
}
