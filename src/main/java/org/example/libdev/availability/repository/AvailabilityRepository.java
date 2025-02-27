package org.example.libdev.availability.repository;

import jakarta.persistence.LockModeType;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBook(Book book);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Availability> findById(Long availabilityId);
}
