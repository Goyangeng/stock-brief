# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Gradle wrapper + 설정 먼저 복사 → 의존성 캐시 활용
COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

# 소스 복사 + 빌드
COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# Stage 2: Runtime (JRE만, 가벼움)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# JVM 메모리 옵션: 컨테이너 메모리의 75%까지 heap 사용 (t3.micro 1GB 대응)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
