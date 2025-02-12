package org.example.libdev.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LibraryResponseDTO {
    private Long libraryId;
    private String libraryCode;
    private String libraryName;
    private String libraryLocation;
    private String libraryPhone;
}
