package org.example.libdev.book.repository;

import org.example.libdev.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findAll(Pageable pageable);

    List<Book> findBySubjectId(Long subjectId);

    List<Book> findByTitleContainingIgnoreCase(String title);
}
