package org.example.libdev.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.libdev.book.dto.BookResponseDTO;
import org.example.libdev.global.entity.BaseEntity;
import org.example.libdev.library.entity.Library;
import org.example.libdev.subject.entity.Subject;

import java.util.List;

@Data
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

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_libraries",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "library_id"))
    private List<Library> libraries;

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
                .build();
    }

    // 책 반납 시 다시 대출 가능 상태로 변경
    public void updateAvailable(Boolean available) {
        this.available = available;
    }
}
