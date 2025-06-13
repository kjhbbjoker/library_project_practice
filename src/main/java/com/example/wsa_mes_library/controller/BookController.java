package com.example.wsa_mes_library.controller;

import com.example.wsa_mes_library.entity.Book;
import com.example.wsa_mes_library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Book API Controller
 * HTTP 요청/응답 처리만 담당 (비즈니스 로직은 Service로 위임)
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    /**
     * 책 목록 조회 (페이징, 정렬, 검색 지원)
     * 
     * @param keyword 검색 키워드 (이름, 작가, 설명에서 검색)
     * @param author 작가명 정확 매칭
     * @param pageable 페이징 정보 (page, size, sort)
     * @return 책 페이징 결과
     * 
     * 사용 예시:
     * GET /api/books?page=0&size=10&sort=name,asc&sort=createdAt,desc
     * GET /api/books?keyword=java&sort=author,asc
     * GET /api/books?author=남궁성&page=1&size=5
     */
    @GetMapping
    public ResponseEntity<Page<Book>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @PageableDefault(size = 1000, sort = "createdAt") Pageable pageable
    ) {
        Page<Book> books = bookService.getBooks(keyword, author, pageable);
        return ResponseEntity.ok(books);
    }
    
    /**
     * 책 단건 조회 (ID로)
     * 
     * @param id 책 ID
     * @return 책 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        
        return book
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * ISBN으로 책 조회
     * 
     * @param isbn ISBN 번호
     * @return 책 정보
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        
        return book
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 작가별 책 개수 조회
     * 
     * @param author 작가명
     * @return 책 개수
     */
    @GetMapping("/count/author/{author}")
    public ResponseEntity<Long> getBookCountByAuthor(@PathVariable String author) {
        long count = bookService.getBookCountByAuthor(author);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 전체 활성 책 개수 조회
     * 
     * @return 전체 책 개수
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalBookCount() {
        long count = bookService.getTotalActiveBookCount();
        return ResponseEntity.ok(count);
    }
    
    /**
     * 검색 결과 미리보기 (제한된 결과)
     * 
     * @param keyword 검색 키워드
     * @param limit 결과 제한 개수 (기본 5개, 최대 10개)
     * @return 제한된 검색 결과
     */
    @GetMapping("/search/preview")
    public ResponseEntity<Page<Book>> searchPreview(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int limit
    ) {
        Page<Book> books = bookService.getSearchPreview(keyword, limit);
        return ResponseEntity.ok(books);
    }
    
    /**
     * 최신 책 조회
     * 
     * @param limit 조회할 개수 (기본 10개, 최대 50개)
     * @return 최신 책 목록
     */
    @GetMapping("/latest")
    public ResponseEntity<Page<Book>> getLatestBooks(
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<Book> books = bookService.getLatestBooks(limit);
        return ResponseEntity.ok(books);
    }
    
    /**
     * 책 존재 여부 확인
     * 
     * @param id 책 ID
     * @return 존재 여부 (200: 존재, 404: 없음)
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> checkBookExists(@PathVariable Long id) {
        boolean exists = bookService.existsById(id);
        
        return exists 
                ? ResponseEntity.ok().build() 
                : ResponseEntity.notFound().build();
    }
    
    /**
     * 작가의 책 존재 여부 확인
     * 
     * @param author 작가명
     * @return 존재 여부 (200: 존재, 404: 없음)
     */
    @GetMapping("/author/{author}/exists")
    public ResponseEntity<Void> checkAuthorHasBooks(@PathVariable String author) {
        boolean hasBooks = bookService.hasBooksByAuthor(author);
        
        return hasBooks 
                ? ResponseEntity.ok().build() 
                : ResponseEntity.notFound().build();
    }


    // 이후 요청용
    @GetMapping("/infinite")
    public ResponseEntity<List<Book>> getBooksForInfiniteScroll(
            @RequestParam(required = false, name = "lastId") Long lastId,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        List<Book> books = bookService.getBooksForInfiniteScroll(lastId, size);
        return ResponseEntity.ok(books);
    }
    
    /**
     * 새 책 등록
     * 
     * @param book 등록할 책 정보
     * @return 등록된 책 정보
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(201).body(createdBook);
    }
    
    /**
     * 책 정보 수정
     * 
     * @param id 수정할 책 ID
     * @param book 수정할 정보
     * @return 수정된 책 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(updatedBook);
    }
    
    /**
     * 책 삭제
     * 
     * @param id 삭제할 책 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
