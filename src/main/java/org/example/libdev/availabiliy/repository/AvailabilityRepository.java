package org.example.libdev.availabiliy.repository;

import org.example.libdev.availabiliy.entity.Availability;
import org.example.libdev.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByBook(Book book);
    
}
