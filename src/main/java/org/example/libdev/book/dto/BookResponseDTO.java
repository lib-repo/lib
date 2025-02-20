package org.example.libdev.book.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
}
