package com.example.wsa_mes_library.controller;

import com.example.wsa_mes_library.entity.Loan;
import com.example.wsa_mes_library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Loan API Controller
 */
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    
    private final LoanService loanService;
    
    /**
     * 대출 목록 조회
     * 
     * @param status 대출 상태 필터
     * @param pageable 페이징 정보
     * @return 대출 페이징 결과
     */
    @GetMapping
    public ResponseEntity<Page<Loan>> getLoans(
            @RequestParam(required = false) Loan.LoanStatus status,
            @PageableDefault(size = 20, sort = "loanDate") Pageable pageable
    ) {
        Page<Loan> loans = loanService.getLoans(status, pageable);
        return ResponseEntity.ok(loans);
    }
    
    /**
     * 대출 단건 조회
     * 
     * @param id 대출 ID
     * @return 대출 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoan(@PathVariable Long id) {
        Optional<Loan> loan = loanService.getLoanById(id);
        
        return loan
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 사용자별 대출 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자의 대출 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable Long userId) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }
    
    /**
     * 도서별 대출 목록 조회
     * 
     * @param bookId 도서 ID
     * @return 도서의 대출 목록
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Loan>> getLoansByBook(@PathVariable Long bookId) {
        List<Loan> loans = loanService.getLoansByBookId(bookId);
        return ResponseEntity.ok(loans);
    }
    
    /**
     * 연체 목록 조회
     * 
     * @return 연체된 대출 목록
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        List<Loan> overdueLoans = loanService.getOverdueLoans();
        return ResponseEntity.ok(overdueLoans);
    }
    
    /**
     * 도서 대출
     * 
     * @param userId 사용자 ID
     * @param bookId 도서 ID
     * @return 생성된 대출 정보
     */
    @PostMapping
    public ResponseEntity<Loan> createLoan(
            @RequestParam Long userId,
            @RequestParam Long bookId
    ) {
        Loan loan = loanService.createLoan(userId, bookId);
        return ResponseEntity.status(201).body(loan);
    }
    
    /**
     * 도서 반납
     * 
     * @param id 대출 ID
     * @return 반납 처리된 대출 정보
     */
    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        Loan loan = loanService.returnBook(id);
        return ResponseEntity.ok(loan);
    }
    
    /**
     * 연체 상태 업데이트 (관리자용)
     * 
     * @return 업데이트 결과
     */
    @PutMapping("/update-overdue")
    public ResponseEntity<Void> updateOverdueLoans() {
        loanService.updateOverdueLoans();
        return ResponseEntity.ok().build();
    }
    
    /**
     * 사용자의 활성 대출 수 조회
     * 
     * @param userId 사용자 ID
     * @return 활성 대출 수
     */
    @GetMapping("/user/{userId}/active-count")
    public ResponseEntity<Long> getActiveLoanCount(@PathVariable Long userId) {
        long count = loanService.getActiveLoanCount(userId);
        return ResponseEntity.ok(count);
    }
}
