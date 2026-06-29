package com.ltfullstack.bookservice.command.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestModel {

    private String id;

    @NotBlank(message = "Author is mandatory ")
    @Size(min = 2 , max = 30 , message = "Name must between 2 and 30 character ")
    private  String name;

    @NotBlank(message = "Athor is mandatory ")
    private String author;

    private  Boolean isReady;

}
