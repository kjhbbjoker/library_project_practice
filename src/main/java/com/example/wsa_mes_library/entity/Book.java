package com.example.wsa_mes_library.entity;

import com.example.wsa_mes_library.lib.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {

    private String name;

    private String author;

    private String isbn;

    private String description;


}
