package com.example.wsa_mes_library.lib;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 기본 Repository 인터페이스 (기존 유지)
 * JPA Repository 기능만 제공
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    
    // JpaRepository의 기본 CRUD 기능만 제공
    // save(), findById(), delete() 등
}
