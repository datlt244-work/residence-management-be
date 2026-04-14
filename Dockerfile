FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

COPY domain/build.gradle domain/
COPY app/build.gradle app/
COPY infra/build.gradle infra/
COPY interface/build.gradle interface/
COPY bootstrap/build.gradle bootstrap/

RUN chmod +x gradlew && sed -i 's/\r$//' gradlew && ./gradlew dependencies --no-daemon -q || true

COPY domain/src domain/src
COPY app/src app/src
COPY infra/src infra/src
COPY interface/src interface/src
COPY bootstrap/src bootstrap/src

RUN ./gradlew :bootstrap:build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /app/bootstrap/build/libs/application.war app.war

RUN chown spring:spring app.war

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.war"]
