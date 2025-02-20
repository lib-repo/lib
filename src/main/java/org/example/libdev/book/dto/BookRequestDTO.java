package org.example.libdev.book.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.libdev.book.entity.Book;

@Getter
@Setter
@Builder
public class BookRequestDTO {
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String publicationYear;
    private String imageUrl;
    private String description;

    public Book toEntity() {
        return Book.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .publisher(publisher)
                .publicationYear(publicationYear)
                .imageUrl(imageUrl)
                .description(description)
                .build();
    }
}
