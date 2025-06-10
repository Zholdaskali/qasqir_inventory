## Базовый образ с Java 20 и Maven
#FROM eclipse-temurin:20-jdk
#
## Устанавливаем рабочую директорию
#WORKDIR /app
#
## Копируем файлы проекта (pom.xml и исходный код)
#COPY pom.xml .
#COPY src ./src
#
## Устанавливаем зависимости и собираем проект
#RUN mvn clean package -DskipTests
#
## Открываем порт 8081
#EXPOSE 8081
#
## Задаем переменные окружения для подключения к БД (можно переопределить при запуске)
#ENV SPRING_DATASOURCE_USERNAME=postgres
#ENV SPRING_DATASOURCE_PASSWORD=erke
#
## Команда для запуска приложения
#CMD ["java", "-jar", "target/QasqirInventory-0.0.1-SNAPSHOT.jar"]

FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/QasqirInventory-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]