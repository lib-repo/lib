package org.example.libdev.rent.repository;

import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.entity.RentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {

    Page<Rent> findByUser_UserIdx(Long userId, Pageable pageable);
    List<Rent> findByStatus(RentStatus status);
    Page<Rent> findByBookTitleContaining(String bookTitle, Pageable pageable);
}
