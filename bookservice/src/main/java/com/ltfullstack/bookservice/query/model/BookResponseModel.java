package com.ltfullstack.bookservice.query.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookResponseModel {
    private String id;

    private  String name;

    private  String author;

    private  Boolean isReady;

    private String imageUrl;

    private String description;

    private String publisher;

    private Integer publishedYear;

    private String genre;
}
