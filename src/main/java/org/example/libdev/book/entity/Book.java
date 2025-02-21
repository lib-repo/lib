package org.example.libdev.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.libdev.availabiliy.entity.Availability;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.global.entity.BaseEntity;
import org.example.libdev.subject.entity.Subject;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book extends BaseEntity {

    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "publication_year", nullable = false)
    private String publicationYear;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "description", nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<Availability> bookAvailabilities;

    public BookResponseDTO toResponseDTO() {
        return BookResponseDTO.builder()
                .bookId(bookId)
                .title(title)
                .author(author)
                .isbn(isbn)
                .publisher(publisher)
                .publicationYear(publicationYear)
                .imageUrl(imageUrl)
                .description(description)
                .subject(subject)
                .build();
    }
}
