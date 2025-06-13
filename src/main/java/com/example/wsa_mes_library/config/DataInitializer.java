package com.example.wsa_mes_library.config;

import com.example.wsa_mes_library.entity.Book;
import com.example.wsa_mes_library.entity.User;
import com.example.wsa_mes_library.repository.BookRepository;
import com.example.wsa_mes_library.repository.UserRepository;
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
    
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() == 0) {
            log.info("샘플 데이터 생성 시작...");
            createSampleData();
            log.info("샘플 데이터 생성 완료!");
        } else {
            log.info("이미 데이터가 존재합니다. 샘플 데이터 생성을 건너뜁니다.");
        }
    }
    
    private void createSampleData() {
        createSampleBooks();
        createSampleUsers();
    }
    
    private void createSampleBooks() {
        List<Book> books = new ArrayList<>();
        
        String[] authors = {"김영하", "한강", "박민규", "정유정", "김훈", "이문열", "공지영", "신경숙", "은희경", "김애란"};
        String[] publishers = {"문학동네", "창비", "민음사", "은행나무", "열린책들", "현대문학", "삼성출판사", "랜덤하우스", "알에이치코리아", "위즈덤하우스"};
        
        // 프로그래밍 도서
        String[] programmingBooks = {
            "클린 코드", "이펙티브 자바", "리팩토링", "디자인 패턴", "알고리즘 문제 해결 전략",
            "Spring Boot 완벽 가이드", "React 완벽 가이드", "Node.js 교과서", "Vue.js 프로젝트", "TypeScript 프로그래밍"
        };
        
        // 문학 도서
        String[] literatureBooks = {
            "채식주의자", "82년생 김지영", "미나리", "소년이 온다", "살인자의 기억법",
            "우리들의 일그러진 영웅", "무진기행", "엄마를 부탁해", "완득이", "7년의 밤"
        };
        
        // 자기계발 도서
        String[] selfDevelopmentBooks = {
            "원씽", "아침 30분 독서", "습관의 힘", "마인드셋", "그릿",
            "성공하는 사람들의 7가지 습관", "데일 카네기 인간관계론", "부의 추월차선", "생각에 관한 생각", "플로우"
        };
        
        int bookId = 1;
        
        // 프로그래밍 도서 생성
        for (String title : programmingBooks) {
            Book book = Book.builder()
                .name(title)
                .author(authors[bookId % authors.length])
                .isbn("978-89-" + String.format("%04d", bookId) + "-001-" + (bookId % 10))
                .description(title + "에 대한 상세한 설명입니다. IT 개발자들에게 필수적인 내용을 다룹니다.")
                .publisher(publishers[bookId % publishers.length])
                .publishYear(2020 + (bookId % 4))
                .available(true)
                .build();
            books.add(book);
            bookId++;
        }
        
        // 문학 도서 생성
        for (String title : literatureBooks) {
            Book book = Book.builder()
                .name(title)
                .author(authors[bookId % authors.length])
                .isbn("978-89-" + String.format("%04d", bookId) + "-002-" + (bookId % 10))
                .description(title + "은 현대 한국 문학의 걸작입니다. 깊이 있는 인물 묘사와 사회적 메시지를 담고 있습니다.")
                .publisher(publishers[bookId % publishers.length])
                .publishYear(2015 + (bookId % 8))
                .available(bookId % 3 != 0) // 일부는 대출 중
                .build();
            books.add(book);
            bookId++;
        }
        
        // 자기계발 도서 생성
        for (String title : selfDevelopmentBooks) {
            Book book = Book.builder()
                .name(title)
                .author(authors[bookId % authors.length])
                .isbn("978-89-" + String.format("%04d", bookId) + "-003-" + (bookId % 10))
                .description(title + "를 통해 개인의 성장과 발전을 도모할 수 있습니다. 실용적인 조언과 사례를 제공합니다.")
                .publisher(publishers[bookId % publishers.length])
                .publishYear(2018 + (bookId % 6))
                .available(true)
                .build();
            books.add(book);
            bookId++;
        }
        
        // 추가 랜덤 도서들
        String[] categories = {"과학", "역사", "철학", "예술", "여행", "요리", "건강", "경제", "심리학", "언어학습"};
        for (int i = 1; i <= 50; i++) {
            Book book = Book.builder()
                .name(categories[i % categories.length] + " 입문서 " + i)
                .author(authors[i % authors.length])
                .isbn("978-89-" + String.format("%04d", bookId) + "-" + String.format("%03d", i) + "-" + (i % 10))
                .description(categories[i % categories.length] + " 분야의 입문서입니다. 초보자도 쉽게 이해할 수 있도록 구성되었습니다.")
                .publisher(publishers[i % publishers.length])
                .publishYear(2010 + (i % 14))
                .available(i % 4 != 0) // 75% 대출 가능
                .build();
            books.add(book);
            bookId++;
        }
        
        bookRepository.saveAll(books);
        log.info("{}개의 샘플 도서 생성 완료", books.size());
    }
    
    private void createSampleUsers() {
        List<User> users = new ArrayList<>();
        
        String[] firstNames = {"민수", "영희", "철수", "순희", "현우", "지영", "승호", "미영", "동현", "수진"};
        String[] lastNames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"};
        String[] domains = {"gmail.com", "naver.com", "daum.net", "kakao.com", "hanmail.net"};
        String[] cities = {"서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "경기", "강원"};
        
        for (int i = 1; i <= 20; i++) {
            String lastName = lastNames[i % lastNames.length];
            String firstName = firstNames[i % firstNames.length];
            String name = lastName + firstName;
            String email = lastName.toLowerCase() + firstName.toLowerCase() + i + "@" + domains[i % domains.length];
            String phone = "010-" + String.format("%04d", 1000 + i) + "-" + String.format("%04d", 1000 + i * 2);
            String address = cities[i % cities.length] + "시 " + firstName + "구 " + lastName + "로 " + (i * 10) + "번길 " + i;
            
            User user = User.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
            
            users.add(user);
        }
        
        userRepository.saveAll(users);
        log.info("{}개의 샘플 사용자 생성 완료", users.size());
    }
}
