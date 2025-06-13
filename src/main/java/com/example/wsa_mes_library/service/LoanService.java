package com.example.wsa_mes_library.service;

import com.example.wsa_mes_library.entity.Book;
import com.example.wsa_mes_library.entity.Loan;
import com.example.wsa_mes_library.entity.User;
import com.example.wsa_mes_library.repository.BookRepository;
import com.example.wsa_mes_library.repository.LoanRepository;
import com.example.wsa_mes_library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {
    
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    
    private static final int MAX_LOANS_PER_USER = 5; // 사용자당 최대 대출 권수
    private static final int LOAN_PERIOD_DAYS = 14; // 대출 기간 (일)
    
    public Page<Loan> getLoans(Loan.LoanStatus status, Pageable pageable) {
        if (status != null) {
            return loanRepository.findByStatus(status, pageable);
        }
        return loanRepository.findAll(pageable);
    }
    
    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }
    
    public List<Loan> getLoansByBookId(Long bookId) {
        return loanRepository.findByBookId(bookId);
    }
    
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }
    
    @Transactional
    public Loan createLoan(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
            
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다: " + bookId));
        
        // 대출 가능 여부 검증
        validateLoanRequest(user, book);
        
        // 도서 대출 중 상태로 변경
        book.setAvailable(false);
        bookRepository.save(book);
        
        // 대출 정보 생성
        LocalDateTime now = LocalDateTime.now();
        Loan loan = Loan.builder()
            .user(user)
            .book(book)
            .loanDate(now)
            .dueDate(now.plusDays(LOAN_PERIOD_DAYS))
            .status(Loan.LoanStatus.ACTIVE)
            .build();
        
        return loanRepository.save(loan);
    }
    
    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("대출 정보를 찾을 수 없습니다: " + loanId));
        
        if (loan.isReturned()) {
            throw new IllegalStateException("이미 반납된 도서입니다.");
        }
        
        // 반납 처리
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(Loan.LoanStatus.RETURNED);
        
        // 도서 대출 가능 상태로 변경
        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
        
        return loanRepository.save(loan);
    }
    
    @Transactional
    public void updateOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDateTime.now());
        
        for (Loan loan : overdueLoans) {
            loan.setStatus(Loan.LoanStatus.OVERDUE);
        }
        
        loanRepository.saveAll(overdueLoans);
    }
    
    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDateTime.now());
    }
    
    public long getActiveLoanCount(Long userId) {
        return loanRepository.countActiveLoansByUserId(userId);
    }
    
    private void validateLoanRequest(User user, Book book) {
        // 도서 대출 가능 여부 확인
        if (!book.isAvailable()) {
            throw new IllegalStateException("현재 대출 중인 도서입니다.");
        }
        
        // 사용자 대출 권수 제한 확인
        long activeLoans = loanRepository.countActiveLoansByUserId(user.getId());
        if (activeLoans >= MAX_LOANS_PER_USER) {
            throw new IllegalStateException("대출 가능한 권수를 초과했습니다. (최대 " + MAX_LOANS_PER_USER + "권)");
        }
        
        // 동일 도서 중복 대출 방지
        List<Loan> activeBookLoans = loanRepository.findActiveLoansByBookId(book.getId());
        if (!activeBookLoans.isEmpty()) {
            throw new IllegalStateException("이미 대출 중인 도서입니다.");
        }
    }
}
