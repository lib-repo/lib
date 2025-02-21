package org.example.libdev.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.libdev.subject.entity.Subject;

@Getter
@Setter
@Builder
public class BookResponseDTO {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String publicationYear;
    private String imageUrl;
    private String description;
    private Subject subject;
}
