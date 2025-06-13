package com.example.wsa_mes_library.entity;

import com.example.wsa_mes_library.lib.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Loan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
    
    private LocalDateTime loanDate;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime returnDate;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoanStatus status = LoanStatus.ACTIVE;
    
    public enum LoanStatus {
        ACTIVE,     // 대출 중
        RETURNED,   // 반납 완료
        OVERDUE     // 연체
    }
    
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && 
               dueDate != null && 
               LocalDateTime.now().isAfter(dueDate);
    }
    
    public boolean isReturned() {
        return status == LoanStatus.RETURNED;
    }
}
