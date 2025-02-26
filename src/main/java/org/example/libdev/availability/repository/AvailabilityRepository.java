package org.example.libdev.availability.repository;

import org.example.libdev.availability.entity.Availability;
import org.example.libdev.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBook(Book book);
    
}
