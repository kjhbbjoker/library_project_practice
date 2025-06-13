package com.example.wsa_mes_library.entity;

import com.example.wsa_mes_library.lib.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
    
    private String publisher;
    
    private Integer publishYear;
    
    @Builder.Default
    private Boolean available = true;
    
    @Builder.Default
    @OneToMany(mappedBy = "book")
    private List<Loan> loans = new ArrayList<>();
    
    public boolean isAvailable() {
        return available != null && available;
    }


}
