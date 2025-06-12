package com.example.wsa_mes_library.repository;

import com.example.wsa_mes_library.common.QueryRepository;
import com.example.wsa_mes_library.entity.Book;
import com.example.wsa_mes_library.entity.QBook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * Book QueryDSL Repository
 * Q클래스 기반 구현 + QueryRepository의 완전한 기본 구현 활용 (Spring Data Sort 자동 지원)
 */
@Repository
public class BookQueryRepository implements QueryRepository<Book, QBook> {
    
    @Getter
    private final JPAQueryFactory queryFactory;
    
    // Q클래스 static 인스턴스 사용
    private final QBook qBook = QBook.book;
    
    public BookQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
    
    @Override
    public QBook getQEntity() {
        return qBook;
    }
    
    // ===== QueryRepository의 모든 기능 자동 상속 =====
    // findPage(BooleanBuilder, Pageable) - Spring Data Sort 자동 처리
    // findAll(BooleanBuilder) - 조건으로 전체 조회
    // count(BooleanBuilder) - 조건으로 개수 조회
    
    // ===== Book 전용 편의 메서드들 (Q클래스 기반) =====
    
    /**
     * 활성 책 조회
     * URL: ?sort=name,asc&sort=createdAt,desc 자동 지원
     */
    public Page<Book> findActiveBooks(Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBook.active.eq(true));
        
        return findPage(builder, pageable); // Spring Data Sort 자동 적용!
    }
    
    /**
     * 키워드 검색 (Q클래스의 타입 안전성 활용)
     * URL: ?keyword=java&sort=author,asc&sort=createdAt,desc 자동 지원
     */
    public Page<Book> searchByKeyword(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBook.active.eq(true));
        
        if (StringUtils.hasText(keyword)) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(qBook.name.containsIgnoreCase(keyword))
                         .or(qBook.author.containsIgnoreCase(keyword))
                         .or(qBook.description.containsIgnoreCase(keyword));
            builder.and(keywordBuilder);
        }
        
        return findPage(builder, pageable); // Spring Data Sort 자동 적용!
    }
    
    /**
     * 작가별 조회 (Q클래스의 타입 안전성 활용)
     * URL: ?author=김작가&sort=name,asc 자동 지원
     */
    public Page<Book> findByAuthor(String author, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBook.active.eq(true));
        System.out.println(author);
        builder.and(qBook.author.containsIgnoreCase(author));
        
        return findPage(builder, pageable); // Spring Data Sort 자동 적용!
    }
    
    /**
     * ISBN으로 조회 (Q클래스의 타입 안전성 활용)
     */
    public Book findByIsbn(String isbn) {
        return queryFactory
            .selectFrom(qBook)
            .where(qBook.active.eq(true)
                   .and(qBook.isbn.eq(isbn)))
            .fetchOne();
    }
    
    /**
     * 작가별 책 개수 조회
     */
    public long countByAuthor(String author) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBook.active.eq(true));
        builder.and(qBook.author.eq(author));
        
        return count(builder);
    }
    
    /**
     * 복합 조건 검색 예시 (Q클래스의 장점 활용)
     */
    public Page<Book> searchWithComplexConditions(String keyword, String author, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBook.active.eq(true));
        
        // 키워드 조건
        if (StringUtils.hasText(keyword)) {
            builder.and(
                qBook.name.containsIgnoreCase(keyword)
                .or(qBook.description.containsIgnoreCase(keyword))
            );
        }
        
        // 작가 조건
        if (StringUtils.hasText(author)) {
            builder.and(qBook.author.eq(author));
        }
        
        return findPage(builder, pageable);
    }
}