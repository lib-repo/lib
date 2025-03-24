package org.example.libdev.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.global.entity.BaseEntity;
import org.example.libdev.library.dto.LibraryResponseDTO;
import org.example.libdev.rent.entity.Rent;

import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "libraries")
public class Library extends BaseEntity {

    @Id
    @Column(name = "library_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libraryId;

    @Column(name = "library_code")
    private String libraryCode;

    @Column(name = "library_name")
    private String libraryName;

    @Column(name = "library_location")
    private String libraryLocation;

    @Column(name = "library_phone")
    private String libraryPhone;

    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY)
    private List<Availability> bookAvailabilities;

    @OneToMany(mappedBy = "toLibrary", fetch = FetchType.LAZY)
    private List<LibraryAgreement> agreements;

    @OneToMany(mappedBy = "library", fetch = FetchType.LAZY)
    private List<Rent> rents;

    public LibraryResponseDTO toResponseDTO() {
        return LibraryResponseDTO.builder()
                .libraryId(libraryId)
                .libraryCode(libraryCode)
                .libraryName(libraryName)
                .libraryLocation(libraryLocation)
                .libraryPhone(libraryPhone)
                .build();
    }
}
