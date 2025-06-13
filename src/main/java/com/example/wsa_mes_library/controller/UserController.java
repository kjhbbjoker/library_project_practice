package com.example.wsa_mes_library.controller;

import com.example.wsa_mes_library.entity.User;
import com.example.wsa_mes_library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * User API Controller
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 사용자 목록 조회
     * 
     * @param keyword 검색 키워드 (이름, 이메일)
     * @param pageable 페이징 정보
     * @return 사용자 페이징 결과
     */
    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<User> users = userService.getUsers(keyword, pageable);
        return ResponseEntity.ok(users);
    }
    
    /**
     * 사용자 단건 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        
        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 이메일
     * @return 사용자 정보
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        
        return user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 새 사용자 등록
     * 
     * @param user 등록할 사용자 정보
     * @return 등록된 사용자 정보
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }
    
    /**
     * 사용자 정보 수정
     * 
     * @param id 수정할 사용자 ID
     * @param user 수정할 정보
     * @return 수정된 사용자 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * 사용자 삭제
     * 
     * @param id 삭제할 사용자 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 사용자 존재 여부 확인
     * 
     * @param id 사용자 ID
     * @return 존재 여부
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> checkUserExists(@PathVariable Long id) {
        boolean exists = userService.existsById(id);
        
        return exists 
                ? ResponseEntity.ok().build() 
                : ResponseEntity.notFound().build();
    }
    
    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일
     * @return 중복 여부
     */
    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Void> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        
        return exists 
                ? ResponseEntity.ok().build() 
                : ResponseEntity.notFound().build();
    }
    
    /**
     * 전체 사용자 수 조회
     * 
     * @return 전체 사용자 수
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalUserCount() {
        long count = userService.getTotalUserCount();
        return ResponseEntity.ok(count);
    }
}
