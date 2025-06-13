package com.example.wsa_mes_library.config;

import com.example.wsa_mes_library.entity.Book;
import com.example.wsa_mes_library.lib.BaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    // BaseRepository를 Book용으로 사용 (실제로는 BookRepository가 있다면 그걸 사용)
    private final BaseRepository<Book> bookRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() == 0) {
            log.info("샘플 데이터 생성 시작...");
            createSampleBooks();
            log.info("샘플 데이터 생성 완료!");
        } else {
            log.info("이미 데이터가 존재합니다. 샘플 데이터 생성을 건너뜁니다.");
        }
    }
    
    private void createSampleBooks() {
        List<Book> books = new ArrayList<>();
        
        String[] authors = {"김작가", "이작가", "박작가", "최작가", "정작가", "한작가", "조작가", "윤작가", "장작가", "임작가"};
        String[] categories = {"프로그래밍", "소설", "에세이", "자기계발", "역사", "과학", "철학", "예술", "여행", "요리"};
        
        for (int i = 1; i <= 1000; i++) {
            Book book = Book.builder()
                .active(i % 10 != 0) // 10번째마다 비활성
                .author(authors[i % authors.length])
                .name(categories[i % categories.length] + " 책제목 " + i)
                .description("이것은 " + i + "번째 책의 상세한 설명입니다. 매우 흥미로운 내용을 담고 있습니다.")
                .isbn("978-89-" + String.format("%04d", i) + "-" + String.format("%03d", i % 1000) + "-" + (i % 10))
                .build();
                
            books.add(book);
            
            // 100개씩 배치로 저장 (메모리 효율)
            if (i % 100 == 0) {
                bookRepository.saveAll(books);
                books.clear();
                log.info("{}개 데이터 저장 완료", i);
            }
        }
        
        // 나머지 데이터 저장
        if (!books.isEmpty()) {
            bookRepository.saveAll(books);
        }
    }
}
