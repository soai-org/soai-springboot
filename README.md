# soai-springboot

## 📝 프로젝트 개요

본 프로젝트는 의료 영상 분석을 위한 다양한 AI 모델을 API 형태로 제공하는 Spring Boot 기반의 서버입니다. 딥러닝 모델을 활용하여 충수염 진단, 의료 영상 분할, 영상 캡셔닝, 그리고 의료 관련 질문에 답변하는 챗봇 기능을 제공하는 SOAI 프로젝트의 백엔드 서버입니다.

## 🚀 주요 기능

- **충수염 진단:** DICOM 이미지로부터 충수염 발병 확률을 예측합니다.
- **이미지 분할:** 의료 영상에서 특정 영역을 분할(Segmentation)하여 시각화합니다.
- **이미지 메타데이터 분석 및 캡셔닝:** DICOM 이미지와 메타데이터를 분석하여 진단 결과를 텍스트로 생성합니다.
- **의료 챗봇:** 미세조정된 언어 모델을 사용하여 의료 관련 질문에 답변합니다.
- **사용자 및 대시보드 관리:** 사용자 인증, 인가 및 대시보드 데이터 제공.
- **DICOM 뷰어 지원:** Orthanc 서버와 연동하여 DICOM 파일(환자, 연구, 시리즈, 이미지) 정보를 제공합니다.

## 🛠️ 기술 스택

- **백엔드:** Spring Boot, Spring Security, Spring AOP
- **데이터베이스:** MariaDB, MyBatis
- **인증:** JWT (JSON Web Token)
- **DICOM:** dcm4che
- **빌드:** Gradle

## ⚙️ 설치 및 실행

### 1. 프로젝트 클론

```bash
git clone https://github.com/soai-org/soai-springboot.git
cd soai-springboot
```

### 2. 환경 변수 설정

`src/main/resources/application.properties` 파일을 열어 아래와 같이 필요한 환경 변수를 설정합니다.

```properties
# Database
spring.datasource.url=jdbc:mariadb://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password

# Orthanc
orthanc.url=http://your_orthanc_server_url
orthanc.username=your_orthanc_username
orthanc.password=your_orthanc_password

# JWT
jwt.secret=your_jwt_secret_key
```

### 3. 프로젝트 빌드

```bash
./gradlew build
```

### 4. 서버 실행

빌드가 완료되면 `build/libs` 디렉토리에 `war` 파일이 생성됩니다. Tomcat과 같은 WAS(Web Application Server)에 배포하여 실행할 수 있습니다.

또는, 내장 Tomcat을 사용하여 개발 환경에서 다음과 같이 실행할 수 있습니다.

```bash
./gradlew bootRun
```

서버가 정상적으로 실행되면 `http://localhost:8080` (또는 설정한 포트)에서 애플리케이션이 실행됩니다.

## 🔗 API 엔드포인트

### 👤 로그인 및 인증 (Login & Auth)

- `POST /login`: 사용자 로그인 (JWT 토큰 발급)
- `POST /reissue`: JWT 토큰 재발급
- `POST /logout`: 사용자 로그아웃

### 🤖 AI 도구 (AI Tools)

- `POST /api/v1/aitools/captioning`: 의료 영상 캡셔닝
- `POST /api/v1/aitools/segmentation`: 의료 영상 분할

### 💬 챗봇 (Chat-Bot)

- `POST /api/v1/chat/ask`: 의료 관련 질문
- `GET /api/v1/chat/history`: 채팅 기록 조회

### 📊 대시보드 (Dashboard)

- `GET /api/v1/dashboard/main`: 대시보드 데이터 조회

### 👥 사용자 관리 (User Management)

- `GET /api/v1/user/list`: 사용자 목록 조회
- `GET /api/v1/user/{user_id}`: 특정 사용자 정보 조회
- `POST /api/v1/user`: 신규 사용자 등록
- `PUT /api/v1/user`: 사용자 정보 수정
- `DELETE /api/v1/user/{user_id}`: 사용자 삭제

### 🖼️ DICOM 뷰어 (Viewer)

- `GET /api/v1/viewer/patient/list`: 환자 목록 조회
- `GET /api/v1/viewer/study/list`: 특정 환자의 연구(Study) 목록 조회
- `GET /api/v1/viewer/series/list`: 특정 연구의 시리즈(Series) 목록 조회
- `GET /api/v1/viewer/instance/list`: 특정 시리즈의 인스턴스(Instance) 목록 조회
- `GET /api/v1/viewer/instance/{instanceId}/image`: 특정 인스턴스의 이미지(PNG) 조회

## 📁 디렉터리 구조

```
.
├── build.gradle              # 프로젝트 의존성 및 빌드 설정
├── gradlew                   # Gradle Wrapper
├── README.md                 # 프로젝트 설명서
└── src
    └── main
        ├── java/com/team1/soai
        │   ├── MainApplication.java    # Spring Boot 메인 애플리케이션
        │   ├── aspect                  # AOP 관련 로직
        │   ├── config                  # 애플리케이션 설정
        │   ├── controller              # API 엔드포인트 컨트롤러
        │   ├── dto                     # 데이터 전송 객체
        │   ├── exception               # 예외 처리
        │   ├── jwtTemple               # JWT 관련 로직
        │   ├── mapper                  # MyBatis 매퍼 인터페이스
        │   └── service                 # 비즈니스 로직
        └── resources
            ├── application.properties  # 애플리케이션 환경 설정
            └── mapper                  # MyBatis XML 매퍼
```