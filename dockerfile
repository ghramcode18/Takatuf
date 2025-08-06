# -------- Stage 1: Build --------
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# انسخي ملفات dependencies فقط (عشان الكاش)
COPY pom.xml .

# تأكدي إن المجلد موجود قبل النسخ (لمنع خطأ)
RUN mkdir -p src/main/resources
COPY src/main/resources/application*.yml ./src/main/resources/

# نزّلي الـ dependencies فقط بدون build
RUN mvn dependency:go-offline

# نسخي بقية المشروع (بما فيها src)
COPY . .

# ابني المشروع
RUN mvn clean package -DskipTests


# -------- Stage 2: Runtime --------
FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

# نسخي الـ jar من مرحلة الـ build
COPY --from=build /app/target/takatuf-0.0.1-SNAPSHOT.jar takatuf.jar

EXPOSE 8080

# شغّل التطبيق
ENTRYPOINT ["java", "-jar", "takatuf.jar"]
