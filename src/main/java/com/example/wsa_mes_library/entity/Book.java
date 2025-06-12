package com.example.wsa_mes_library.entity;

import com.example.wsa_mes_library.lib.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Book extends BaseEntity {

    private String name;

    private String author;

    private String isbn;

    private String description;
}
