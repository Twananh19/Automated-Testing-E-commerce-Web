# E-Commerce Web Application — Automated Testing Project

Dự án kiểm thử tự động cho ứng dụng web thương mại điện tử sử dụng Spring Boot, JUnit 5, Mockito, Selenium WebDriver, JaCoCo và PIT Mutation Testing.

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.2.5 |
| Template Engine | Thymeleaf | (via starter) |
| Authentication | Spring Security | (via starter) |
| Database | H2 (in-memory) | 2.x |
| ORM | Spring Data JPA | (via starter) |
| Unit Testing | JUnit 5 + Mockito | 5.10.x |
| Property-Based Testing | jqwik | 1.8.4 |
| Integration Testing | MockMvc + @WebMvcTest | (via spring-boot-starter-test) |
| E2E Testing | Selenium WebDriver | 4.27.0 |
| Driver Management | WebDriverManager | 5.9.2 |
| Code Coverage | JaCoCo | 0.8.12 |
| Mutation Testing | PIT | 1.15.3 |
| Build Tool | Maven | 3.9+ |
| CI/CD | GitHub Actions | - |
| Java | JDK | 17+ |

## Prerequisites

- **JDK**: 17 trở lên (khuyến nghị OpenJDK 17 hoặc 21)
- **Maven**: 3.9+
- **Google Chrome**: phiên bản mới nhất (cho Selenium tests)
- **RAM**: tối thiểu 4GB

## Cấu trúc dự án

```
ecommerce-webapp/
├── pom.xml
├── README.md
├── .github/workflows/ci.yml           (CI/CD)
├── docs/                               (Tài liệu kiểm thử)
│   ├── requirements.md                 (Đặc tả yêu cầu)
│   ├── test-plan.md                    (Kế hoạch kiểm thử IEEE 829)
│   ├── test-case-spec.md               (Bảng test case)
│   ├── traceability-matrix.md          (Ma trận truy xuất)
│   └── demo-guide.md                   (Hướng dẫn demo)
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── EcommerceApplication.java
│   │   │   ├── config/
│   │   │   │   ├── DataSeeder.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   └── test/
│       ├── java/com/ecommerce/
│       │   ├── unit/                    (Unit Tests — JUnit 5 + Mockito + jqwik)
│       │   ├── integration/             (Integration Tests — MockMvc)
│       │   └── selenium/                (E2E Tests — Selenium WebDriver)
│       └── resources/
│           └── application-test.properties
```

## Setup & Run

```bash
# 1. Biên dịch project
mvn compile

# 2. Chạy ứng dụng (http://localhost:8080)
mvn spring-boot:run

# 3. Chạy Unit + Integration Tests
mvn test

# 4. Chạy tất cả tests (bao gồm Selenium E2E)
mvn verify

# 5. Generate JaCoCo coverage report
mvn jacoco:report
# Mở target/site/jacoco/index.html

# 6. Chạy PIT Mutation Testing
mvn org.pitest:pitest-maven:mutationCoverage
# Mở target/pit-reports/index.html
```

## Tài khoản test

| Username | Password | Role |
|----------|----------|------|
| standard_user | secret_sauce | USER |
| admin_user | admin_pass | USER, ADMIN |
| performance_glitch_user | secret_sauce | USER |

## Mã voucher test

| Code | Discount | Trạng thái |
|------|----------|-----------|
| SAVE10 | 10% | Hợp lệ |
| SAVE20 | 20% | Hợp lệ |
| SAVE50 | 50% | Hợp lệ |
| EXPIRED2024 | - | Hết hạn |

## Testing Pyramid

```
          /\
         /E2E\          8 cases (Selenium WebDriver)
        /──────\
       / Integ  \       15 cases (MockMvc standalone)
      /──────────\
     / Unit+PBT   \    59 cases (JUnit 5 + Mockito + jqwik)
    /──────────────\

    Tổng: 82 test cases
```

## Kỹ thuật kiểm thử áp dụng

- **Boundary Value Analysis (BVA)** — Kiểm tra giá trị biên
- **Equivalence Partitioning (EP)** — Phân lớp tương đương
- **Decision Table Testing** — Bảng quyết định
- **State Transition Testing** — Kiểm thử chuyển trạng thái
- **Property-Based Testing** — Kiểm thử dựa trên tính chất
- **Pairwise / Combinatorial Testing** — Kiểm thử tổ hợp
- **White-box: Path Coverage** — Bao phủ đường đi
- **Mutation Testing** — Kiểm thử đột biến (PIT)
- **Exploratory Testing** — Kiểm thử khám phá

## Tác giả

Bài tập lớn môn Kiểm thử Phần mềm
