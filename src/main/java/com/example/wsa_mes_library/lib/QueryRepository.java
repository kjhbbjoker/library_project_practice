package com.example.wsa_mes_library.lib;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * QueryDSL 전용 Repository 인터페이스
 * Q클래스 기반 설계 + BooleanBuilder 중심 + Spring Data Sort 자동 지원
 */
public interface QueryRepository<T extends BaseEntity, Q extends EntityPathBase<T>> {
    
    Logger log = LoggerFactory.getLogger(QueryRepository.class);
    
    /**
     * JPAQueryFactory 반환 (구현체에서 제공)
     */
    JPAQueryFactory getQueryFactory();
    
    /**
     * Q클래스 인스턴스 반환 (구현체에서 제공)
     */
    Q getQEntity();
    
    /**
     * BooleanBuilder + 페이징으로 데이터 조회 (Spring Data Sort 지원)
     */
    default Page<T> findPage(BooleanBuilder builder, Pageable pageable) {
        JPAQueryFactory queryFactory = getQueryFactory();
        Q qEntity = getQEntity();
        
        // 메인 쿼리
        JPAQuery<T> query = queryFactory.selectFrom(qEntity);
        
        // BooleanBuilder 조건 적용
        if (builder != null && builder.hasValue()) {
            query.where(builder);
        }
        
        // Spring Data Sort 자동 처리
        applySorting(query, qEntity, pageable);
        
        // 개수 조회
        long total = count(builder);
        
        // 페이징 적용
        List<T> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        
        return new PageImpl<>(content, pageable, total);
    }
    
    /**
     * BooleanBuilder + 페이징 + 커스텀 정렬 (OrderSpecifier 우선)
     */
    default Page<T> findPage(BooleanBuilder builder, Pageable pageable, OrderSpecifier<?>... orders) {
        JPAQueryFactory queryFactory = getQueryFactory();
        Q qEntity = getQEntity();
        
        // 메인 쿼리
        JPAQuery<T> query = queryFactory.selectFrom(qEntity);
        
        // BooleanBuilder 조건 적용
        if (builder != null && builder.hasValue()) {
            query.where(builder);
        }
        
        // 커스텀 정렬이 있으면 우선 적용, 없으면 Spring Data Sort 적용
        if (orders != null && orders.length > 0) {
            query.orderBy(orders);
        } else {
            applySorting(query, qEntity, pageable);
        }
        
        // 개수 조회
        long total = count(builder);
        
        // 페이징 적용
        List<T> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        
        return new PageImpl<>(content, pageable, total);
    }
    
    /**
     * BooleanBuilder로 전체 데이터 조회
     */
    default List<T> findAll(BooleanBuilder builder) {
        JPAQueryFactory queryFactory = getQueryFactory();
        Q qEntity = getQEntity();
        
        JPAQuery<T> query = queryFactory.selectFrom(qEntity);
        
        if (builder != null && builder.hasValue()) {
            query.where(builder);
        }
        
        return query.fetch();
    }
    
    /**
     * BooleanBuilder로 개수 조회
     */
    default long count(BooleanBuilder builder) {
        JPAQueryFactory queryFactory = getQueryFactory();
        Q qEntity = getQEntity();
        
        JPAQuery<Long> countQuery = queryFactory
            .select(qEntity.count())
            .from(qEntity);
        
        if (builder != null && builder.hasValue()) {
            countQuery.where(builder);
        }
        
        Long result = countQuery.fetchOne();
        return result != null ? result : 0L;
    }
    
    /**
     * Spring Data Sort를 QueryDSL OrderSpecifier로 변환 후 적용
     */
    default void applySorting(JPAQuery<T> query, Q qEntity, Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return; // 기본 정렬 없음
        }

        pageable.getSort().forEach(order -> {
            try {
                String property = order.getProperty();
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                // 정렬용으로만 PathBuilder 사용
                PathBuilder<T> pathBuilder = new PathBuilder<>(
                        qEntity.getType(),
                        qEntity.toString()
                );

                OrderSpecifier<?> orderSpec = new OrderSpecifier<>(
                        direction,
                        pathBuilder.get(property, Comparable.class)
                );
                query.orderBy(orderSpec);
            } catch (Exception e) {
                log.warn("정렬 적용 실패 - 필드: {}, 오류: {}", order.getProperty(), e.getMessage());
            }
        });
    }
    
    /**
     * Predicate + 페이징 (호환성)
     */
    default Page<T> findPage(Predicate condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (condition != null) {
            builder.and(condition);
        }
        return findPage(builder, pageable);
    }
    
    /**
     * Predicate로 전체 조회 (호환성)
     */
    default List<T> findAll(Predicate condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (condition != null) {
            builder.and(condition);
        }
        return findAll(builder);
    }
    
    /**
     * Predicate로 개수 조회 (호환성)
     */
    default long count(Predicate condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (condition != null) {
            builder.and(condition);
        }
        return count(builder);
    }
    
    /**
     * 전체 페이징 조회 (조건 없음)
     */
    default Page<T> findAll(Pageable pageable) {
        return findPage(new BooleanBuilder(), pageable);
    }
    
    /**
     * ID 기반 커서 페이징 (무한 스크롤용)
     * @param lastId 마지막으로 본 ID (null이면 처음부터)
     * @param limit 가져올 개수
     * @param builder 추가 조건 (선택사항)
     * @return ID보다 작은 데이터들을 ID 내림차순으로 반환
     */
    default List<T> findByIdCursor(Long lastId, int limit, BooleanBuilder builder) {
        JPAQueryFactory queryFactory = getQueryFactory();
        Q qEntity = getQEntity();
        
        JPAQuery<T> query = queryFactory.selectFrom(qEntity);
        
        // 기본 조건 적용
        if (builder != null && builder.hasValue()) {
            query.where(builder);
        }
        
        // ID 커서 조건 추가
        if (lastId != null) {
            // PathBuilder로 ID 필드 접근
            PathBuilder<T> pathBuilder = new PathBuilder<>(qEntity.getType(), qEntity.toString());
            query.where(pathBuilder.getNumber("id", Long.class).lt(lastId));
        }
        
        return query
            .orderBy(new OrderSpecifier<>(Order.DESC, 
                new PathBuilder<>(qEntity.getType(), qEntity.toString()).getNumber("id", Long.class)))
            .limit(limit)
            .fetch();
    }
    
    /**
     * ID 기반 커서 페이징 (조건 없음)
     */
    default List<T> findByIdCursor(Long lastId, int limit) {
        return findByIdCursor(lastId, limit, null);
    }
}