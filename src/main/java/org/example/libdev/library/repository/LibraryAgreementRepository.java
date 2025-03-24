package org.example.libdev.library.repository;

import org.example.libdev.library.entity.Library;
import org.example.libdev.library.entity.LibraryAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibraryAgreementRepository extends JpaRepository<LibraryAgreement, Long> {
    @Query("SELECT la.toLibrary FROM LibraryAgreement la WHERE la.fromLibrary.libraryId = :libraryId")
    List<Library> findPartnerLibrariesByLibraryId(@Param("libraryId") Long libraryId);

    boolean existsByFromLibrary_LibraryIdAndToLibrary_LibraryId(Long fromLibraryId, Long toLibraryId);

    Optional<LibraryAgreement> findLibraryAgreementByFromLibrary_LibraryIdAndToLibrary_LibraryId(Long fromLibraryId, Long toLibraryId);
}
