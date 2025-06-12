package com.example.wsa_mes_library.lib;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1)
    protected Long id;

    protected Boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    protected String entityType = this.getClass().getSimpleName();


    // 활성화/비활성화 메서드
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    // 논리 삭제 메서드
    public void delete() {
        this.active = false;
    }
}
