package org.example.libdev.availability.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.example.libdev.book.entity.Book;
import org.example.libdev.global.entity.BaseEntity;
import org.example.libdev.library.entity.Library;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book_libraries")
@JsonIgnoreProperties({"book", "library"})
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityId;

    @Column(name = "available")
    private boolean available;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "library_id")
    private Library library;
}
