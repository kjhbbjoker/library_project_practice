package com.example.wsa_mes_library.entity;

import com.example.wsa_mes_library.lib.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String name;
    
    private String email;
    
    private String phone;
    
    private String address;
    
    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Loan> loans = new ArrayList<>();
}
