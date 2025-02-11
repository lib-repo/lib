package org.example.libdev.library.dto;

import lombok.Builder;
import lombok.Data;
import org.example.libdev.library.entity.Library;

@Data
@Builder
public class LibraryRequestDTO {
    private String libraryCode;
    private String libraryName;
    private String libraryLocation;
    private String libraryPhone;

    public Library toEntity() {
        return Library.builder()
                .libraryCode(libraryCode)
                .libraryName(libraryName)
                .libraryLocation(libraryLocation)
                .libraryPhone(libraryPhone)
                .build();
    }
}
