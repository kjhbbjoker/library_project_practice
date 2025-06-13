package com.example.wsa_mes_library.repository;

import com.example.wsa_mes_library.entity.Loan;
import com.example.wsa_mes_library.lib.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends BaseRepository<Loan> {
    
    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book WHERE l.user.id = :userId")
    List<Loan> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book WHERE l.book.id = :bookId")
    List<Loan> findByBookId(@Param("bookId") Long bookId);
    
    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book WHERE l.status = :status")
    Page<Loan> findByStatus(@Param("status") Loan.LoanStatus status, Pageable pageable);
    
    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book " +
           "WHERE l.status = 'ACTIVE' AND l.dueDate < :now")
    List<Loan> findOverdueLoans(@Param("now") LocalDateTime now);
    
    @Query("SELECT l FROM Loan l JOIN FETCH l.user JOIN FETCH l.book " +
           "WHERE l.book.id = :bookId AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByBookId(@Param("bookId") Long bookId);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    long countActiveLoansByUserId(@Param("userId") Long userId);
}
