package com.project.dto;

import com.project.entity.Author;
import com.project.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    private String isbn;
    private String serialName;

//    @NotNull(message = "Author is mandatory")
    private Long author_id;



    private String author_name;

    private String author_description;

    private Set<Long> categories = new HashSet<>();
    private Set<Long> publishers = new HashSet<>();

}
