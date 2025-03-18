package org.example.libdev.library.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "LibraryAgreements")
@Getter
public class LibraryAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId;

    @ManyToOne
    @JoinColumn(name = "from_library_id")
    private Library fromLibrary;

    @ManyToOne
    @JoinColumn(name = "to_library_id")
    private Library toLibrary;

    private String agreementDate;

}
