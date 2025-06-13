package com.example.wsa_mes_library.service;

import com.example.wsa_mes_library.entity.Book;
import com.example.wsa_mes_library.repository.BookQueryRepository;
import com.example.wsa_mes_library.repository.BookRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Book 비즈니스 로직 서비스
 * Controller와 Repository 사이의 비즈니스 로직 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookService {
    
    private final BookQueryRepository bookQueryRepository;

    private final BookRepository bookRepository;
    
    /**
     * 책 목록 조회 (비즈니스 로직 포함)
     * 
     * @param keyword 검색 키워드
     * @param author 작가명
     * @param pageable 페이징 정보
     * @return 책 페이징 결과
     */
    public Page<Book> getBooks(String keyword, String author, Pageable pageable) {
        log.debug("책 목록 조회 - keyword: {}, author: {}, page: {}", keyword, author, pageable.getPageNumber());
        
        // 비즈니스 로직: 검색 조건에 따른 분기 처리
        if (StringUtils.hasText(keyword) && StringUtils.hasText(author)) {
            log.debug("복합 검색 수행: keyword={}, author={}", keyword, author);
            return bookQueryRepository.searchWithComplexConditions(keyword, author, pageable);
        } else if (StringUtils.hasText(keyword)) {
            log.debug("키워드 검색 수행: {}", keyword);
            return bookQueryRepository.searchByKeyword(keyword, pageable);
        } else if (StringUtils.hasText(author)) {
            log.debug("작가별 검색 수행: {}", author);
            return bookQueryRepository.findByAuthor(author, pageable);
        } else {
            log.debug("전체 활성 책 조회");
            return bookQueryRepository.findActiveBooks(pageable);
        }
    }
    
    /**
     * 책 단건 조회 (ID로)
     * 
     * @param id 책 ID
     * @return 책 정보 (Optional)
     */
    public Optional<Book> getBookById(Long id) {
        log.debug("책 단건 조회 - ID: {}", id);
        
        if (id == null || id <= 0) {
            log.warn("잘못된 책 ID: {}", id);
            return Optional.empty();
        }
        
        Book book = bookQueryRepository.getQueryFactory()
                .selectFrom(bookQueryRepository.getQEntity())
                .where(bookQueryRepository.getQEntity().id.eq(id)
                      .and(bookQueryRepository.getQEntity().active.eq(true)))
                .fetchOne();
        
        if (book == null) {
            log.info("책을 찾을 수 없음 - ID: {}", id);
        }
        
        return Optional.ofNullable(book);
    }
    
    /**
     * ISBN으로 책 조회
     * 
     * @param isbn ISBN 번호
     * @return 책 정보 (Optional)
     */
    public Optional<Book> getBookByIsbn(String isbn) {
        log.debug("ISBN으로 책 조회: {}", isbn);
        
        if (!StringUtils.hasText(isbn)) {
            log.warn("빈 ISBN 값");
            return Optional.empty();
        }
        
        // 비즈니스 로직: ISBN 형식 검증 (간단한 예시)
        if (!isValidIsbn(isbn)) {
            log.warn("잘못된 ISBN 형식: {}", isbn);
            return Optional.empty();
        }
        
        Book book = bookQueryRepository.findByIsbn(isbn);
        
        if (book == null) {
            log.info("ISBN에 해당하는 책을 찾을 수 없음: {}", isbn);
        }
        
        return Optional.ofNullable(book);
    }
    
    /**
     * 작가별 책 개수 조회
     * 
     * @param author 작가명
     * @return 책 개수
     */
    public long getBookCountByAuthor(String author) {
        log.debug("작가별 책 개수 조회: {}", author);
        
        if (!StringUtils.hasText(author)) {
            log.warn("빈 작가명");
            return 0L;
        }
        
        long count = bookQueryRepository.countByAuthor(author);
        log.debug("작가 '{}' 의 책 개수: {}", author, count);
        
        return count;
    }
    
    /**
     * 전체 활성 책 개수 조회
     * 
     * @return 전체 책 개수
     */
    public long getTotalActiveBookCount() {
        log.debug("전체 활성 책 개수 조회");

        long count = bookQueryRepository.count(bookQueryRepository.getQEntity().active.eq(true));
        log.debug("전체 활성 책 개수: {}", count);
        
        return count;
    }
    
    /**
     * 검색 결과 미리보기
     * 
     * @param keyword 검색 키워드
     * @param limit 결과 제한 개수
     * @return 제한된 검색 결과
     */
    public Page<Book> getSearchPreview(String keyword, int limit) {
        log.debug("검색 미리보기 - keyword: {}, limit: {}", keyword, limit);
        
        if (!StringUtils.hasText(keyword)) {
            log.warn("빈 검색 키워드");
            return Page.empty();
        }
        
        // 비즈니스 로직: 미리보기는 최대 10개로 제한
        int actualLimit = Math.min(limit, 10);
        
        return bookQueryRepository.searchByKeyword(keyword, Pageable.ofSize(actualLimit));
    }
    
    /**
     * 최신 책 조회
     * 
     * @param limit 조회할 개수
     * @return 최신 책 목록
     */
    public Page<Book> getLatestBooks(int limit) {
        log.debug("최신 책 조회 - limit: {}", limit);
        
        // 비즈니스 로직: 최신 책은 최대 50개까지만 허용
        int actualLimit = Math.min(Math.max(limit, 1), 50);
        
        return bookQueryRepository.findActiveBooks(Pageable.ofSize(actualLimit));
    }
    
    /**
     * 책 존재 여부 확인
     * 
     * @param id 책 ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        return getBookById(id).isPresent();
    }
    
    /**
     * 작가의 책이 존재하는지 확인
     * 
     * @param author 작가명
     * @return 존재 여부
     */
    public boolean hasBooksByAuthor(String author) {
        if (!StringUtils.hasText(author)) {
            return false;
        }
        
        return getBookCountByAuthor(author) > 0;
    }
    
    // ===== 유틸리티 메서드들 =====
    
    /**
     * ISBN 형식 검증 (간단한 예시)
     * 실제로는 더 정교한 검증이 필요
     */
    private boolean isValidIsbn(String isbn) {
        if (!StringUtils.hasText(isbn)) {
            return false;
        }
        
        // 간단한 ISBN 형식 검증 (하이픈 포함 13자리 또는 10자리)
        String cleanIsbn = isbn.replaceAll("-", "");
        return cleanIsbn.length() == 10 || cleanIsbn.length() == 13;
    }

    /**
     * ID 기반 커서로 책 목록 조회 (무한 스크롤용)
     *
     * @param lastId 마지막으로 본 책 ID (null이면 첫 요청)
     * @param size 가져올 개수 (기본 20개)
     * @return 책 목록 (ID 내림차순)
     */
    public List<Book> getBooksForInfiniteScroll(Long lastId, Integer size) {
        int limitSize = (size != null && size > 0) ? Math.min(size, 100) : 20; // 최대 100개 제한
        return bookQueryRepository.findByIdCursor(lastId, limitSize);
    }
    
    /**
     * 새 책 등록
     * 
     * @param book 등록할 책 정보
     * @return 등록된 책
     */
    @Transactional
    public Book createBook(Book book) {
        log.debug("새 책 등록: {}", book.getName());
        
        // 비즈니스 로직: 필수 정보 검증
        validateBookData(book);
        
        // ISBN 중복 체크
        if (StringUtils.hasText(book.getIsbn()) &&
            bookQueryRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("이미 존재하는 ISBN입니다: " + book.getIsbn());
        }
        
        // 기본값 설정
        if (book.getAvailable() == null) {
            book.setAvailable(true);
        }
        
        Book savedBook = bookRepository.save(book);
        log.info("새 책 등록 완료 - ID: {}, 제목: {}", savedBook.getId(), savedBook.getName());
        
        return savedBook;
    }
    
    /**
     * 책 정보 수정
     * 
     * @param id 수정할 책 ID
     * @param bookDetails 수정할 정보
     * @return 수정된 책
     */
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        log.debug("책 정보 수정 - ID: {}", id);
        
        Book book = getBookById(id)
            .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + id));
        
        // 비즈니스 로직: 수정 정보 검증
        validateBookData(bookDetails);
        
        // ISBN 변경 시 중복 체크
        if (!book.getIsbn().equals(bookDetails.getIsbn()) && 
            StringUtils.hasText(bookDetails.getIsbn()) &&
            bookQueryRepository.existsByIsbn(bookDetails.getIsbn())) {
            throw new IllegalArgumentException("이미 존재하는 ISBN입니다: " + bookDetails.getIsbn());
        }
        
        // 정보 업데이트
        book.setName(bookDetails.getName());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setDescription(bookDetails.getDescription());
        book.setPublisher(bookDetails.getPublisher());
        book.setPublishYear(bookDetails.getPublishYear());
        
        Book updatedBook = bookRepository.save(book);
        log.info("책 정보 수정 완료 - ID: {}, 제목: {}", updatedBook.getId(), updatedBook.getName());
        
        return updatedBook;
    }
    
    /**
     * 책 삭제 (소프트 삭제)
     * 
     * @param id 삭제할 책 ID
     */
    @Transactional
    public void deleteBook(Long id) {
        log.debug("책 삭제 - ID: {}", id);
        
        Book book = getBookById(id)
            .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + id));
        
        // 비즈니스 로직: 대출 중인 책은 삭제 불가
        if (!book.isAvailable()) {
            throw new IllegalStateException("대출 중인 책은 삭제할 수 없습니다.");
        }
        
        // 소프트 삭제
        book.setActive(false);
        bookRepository.save(book);
        
        log.info("책 삭제 완료 - ID: {}, 제목: {}", book.getId(), book.getName());
    }
    
    /**
     * 책 데이터 검증
     * 
     * @param book 검증할 책 데이터
     */
    private void validateBookData(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("책 정보가 필요합니다.");
        }
        
        if (!StringUtils.hasText(book.getName())) {
            throw new IllegalArgumentException("책 제목은 필수입니다.");
        }
        
        if (!StringUtils.hasText(book.getAuthor())) {
            throw new IllegalArgumentException("저자는 필수입니다.");
        }
        
        if (StringUtils.hasText(book.getIsbn()) && !isValidIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("올바른 ISBN 형식이 아닙니다.");
        }
        
        if (book.getPublishYear() != null && 
            (book.getPublishYear() < 1000 || book.getPublishYear() > java.time.Year.now().getValue())) {
            throw new IllegalArgumentException("올바른 출판연도가 아닙니다.");
        }
    }
}
