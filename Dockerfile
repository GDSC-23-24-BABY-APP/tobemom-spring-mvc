# OpenJDK 17 이미지 사용
FROM openjdk:17-jdk-slim

# Gradle 다운로드 및 설치
RUN mkdir /opt/gradle
WORKDIR /opt/gradle
RUN apt-get update && apt-get install -y curl unzip
RUN curl -L https://services.gradle.org/distributions/gradle-8.5-bin.zip -o gradle-8.5-bin.zip
RUN unzip -d /opt/gradle gradle-8.5-bin.zip
ENV PATH="/opt/gradle/gradle-8.5/bin:${PATH}"



# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 파일 복사
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 애플리케이션 빌드
WORKDIR /app
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN /opt/gradle/gradle-8.5/bin/gradle build

# 엔트리 포인트 설정
CMD ["java", "-jar", "build/libs/tobemom-application.jar"]
