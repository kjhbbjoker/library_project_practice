# WSA MES Library - 도서관 관리 시스템

Spring Boot와 React TypeScript로 구현된 도서관 관리 시스템입니다.

## 🚀 주요 기능

### 📚 도서 관리
- **도서 등록/수정/삭제**: 새로운 도서를 등록하고 기존 도서 정보를 수정/삭제
- **도서 검색**: 제목, 저자, ISBN으로 도서 검색
- **도서 목록**: 페이징과 정렬을 지원하는 도서 목록 조회

### 👥 사용자 관리
- **사용자 등록/수정/삭제**: 도서관 이용자 정보 관리
- **사용자 검색**: 이름, 이메일로 사용자 검색
- **사용자 목록**: 페이징을 지원하는 사용자 목록 조회

### 📋 대출 관리
- **도서 대출**: 사용자가 도서를 대출하는 기능
- **도서 반납**: 대출된 도서의 반납 처리
- **대출 현황**: 대출 중, 반납 완료, 연체 상태별 조회
- **대출 제한**: 사용자당 최대 5권, 중복 대출 방지

### ⚠️ 연체 관리
- **연체 감지**: 자동으로 연체된 대출 감지
- **연체 알림**: 이메일 알림 기능
- **연체 통계**: 연체 현황 요약 정보

## 🛠 기술 스택

### Backend (Spring Boot)
- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **QueryDSL** - 타입 안전한 쿼리 작성
- **MySQL** - 메인 데이터베이스
- **Swagger/OpenAPI 3** - API 문서화
- **Lombok** - 코드 간소화

### Frontend (React TypeScript)
- **React 18**
- **TypeScript**
- **Vite** - 빠른 개발 서버
- **Tailwind CSS** - 유틸리티 우선 스타일링
- **React Router** - 클라이언트 사이드 라우팅
- **React Query** - 서버 상태 관리
- **Axios** - HTTP 클라이언트
- **React Hook Form** - 폼 관리
- **date-fns** - 날짜 처리

## 🏗 프로젝트 구조

```
wsa_mes_library/
├── src/main/java/com/example/wsa_mes_library/
│   ├── config/          # 설정 파일들
│   ├── controller/      # REST API 컨트롤러
│   ├── entity/          # JPA 엔티티
│   ├── repository/      # 데이터 액세스 레이어
│   ├── service/         # 비즈니스 로직
│   └── lib/             # 공통 라이브러리
├── src/main/resources/
│   ├── application.yml  # 애플리케이션 설정
│   └── application-develop.yml
└── frontend/
    ├── src/
    │   ├── components/  # React 컴포넌트
    │   ├── services/    # API 클라이언트
    │   ├── types/       # TypeScript 타입 정의
    │   └── App.tsx      # 메인 앱 컴포넌트
    └── package.json
```

## 🚀 실행 방법

### 1. 데이터베이스 설정
MySQL 데이터베이스를 생성하고 설정:
```sql
CREATE DATABASE wsa_mes_library;
```

### 2. 백엔드 실행
```bash
# 프로젝트 루트에서
./gradlew bootRun
```

백엔드 서버가 `http://localhost:8080`에서 실행됩니다.

### 3. 프론트엔드 실행
```bash
# frontend 디렉토리에서
cd frontend
npm install
npm run dev
```

프론트엔드 서버가 `http://localhost:3000`에서 실행됩니다.

## 📊 API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## 🗄 데이터베이스 ERD

```
User (사용자)
├── id (PK)
├── name
├── email
├── phone
├── address
└── createdAt, updatedAt

Book (도서)
├── id (PK)
├── name
├── author
├── isbn
├── description
├── publisher
├── publishYear
├── available
└── createdAt, updatedAt

Loan (대출)
├── id (PK)
├── user_id (FK -> User)
├── book_id (FK -> Book)
├── loanDate
├── dueDate
├── returnDate
├── status (ACTIVE/RETURNED/OVERDUE)
└── createdAt, updatedAt
```

## 🔧 주요 설정

### 대출 규칙
- **대출 기간**: 14일
- **최대 대출 권수**: 사용자당 5권
- **연체 처리**: 자동으로 연체 상태 업데이트

### 보안 설정
- **CORS**: 개발 환경에서 프론트엔드 접근 허용
- **입력 검증**: Bean Validation 적용
- **예외 처리**: 글로벌 예외 핸들러로 일관된 에러 응답

### 성능 최적화
- **QueryDSL**: 복잡한 쿼리 최적화
- **JPA Batch Fetch**: N+1 쿼리 방지
- **페이징**: 대용량 데이터 처리
- **인덱싱**: 검색 성능 향상

## 🧪 테스트

### 백엔드 테스트
```bash
./gradlew test
```

### 프론트엔드 테스트
```bash
cd frontend
npm run test
```

## 📝 개발 노트

### 아키텍처 특징
- **레이어드 아키텍처**: Controller → Service → Repository 구조
- **QueryDSL 활용**: 타입 안전한 쿼리와 동적 쿼리 작성
- **BaseEntity**: 공통 필드(id, createdAt, updatedAt, active) 상속
- **예외 처리**: 비즈니스 로직에서 발생하는 예외를 적절히 핸들링

### 개발 고려사항
- **확장성**: 새로운 엔티티 추가 시 BaseEntity와 BaseRepository 활용
- **유지보수성**: 코드 컨벤션과 주석을 통한 가독성 향상
- **사용자 경험**: 직관적인 UI/UX 설계
- **데이터 무결성**: 비즈니스 규칙을 통한 데이터 일관성 보장

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 👨‍💻 개발자

- **개발자**: WSA MES Library Team
- **이메일**: whdg1234@gmail.com
- **GitHub**: [프로젝트 저장소 URL]

## 🔮 향후 개발 계획

- [ ] 사용자 권한 관리 (관리자/일반 사용자)
- [ ] 도서 예약 시스템
- [ ] 이메일/SMS 자동 알림
- [ ] 통계 대시보드
- [ ] 모바일 앱 개발
- [ ] 바코드 스캐너 연동
- [ ] 도서 추천 시스템
