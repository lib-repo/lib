package org.example.libdev.rent.repository;

import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {

    Optional<List<Rent>> findByUser_UserIdxAndStatus(Long userIdx, RentStatus status);
    Optional<List<Rent>> findByStatus(RentStatus status);
    Page<Rent> findByBookTitleContaining(String bookTitle, Pageable pageable);
    Optional<List<Rent>> findByUser_UserIdxAndStatusNot(Long userId, RentStatus status);
}
