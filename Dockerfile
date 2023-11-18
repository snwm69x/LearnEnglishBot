# Используйте официальный образ Maven с Java 17 для сборки вашего приложения
FROM maven:3.8.4-openjdk-17 AS build

# Установите рабочую директорию в /app
WORKDIR /app

# Копируйте файлы вашего проекта в контейнер
COPY . .

# Соберите приложение
RUN mvn package

# Используйте официальный образ OpenJDK 17 для запуска вашего приложения
FROM openjdk:17-jdk-slim

# Установите рабочую директорию в /app
WORKDIR /app

# Копируйте jar файл из стадии сборки в текущую стадию
COPY --from=build /app/target/englishbot-0.0.1-SNAPSHOT.jar app.jar

# Откройте порт, на котором работает ваше приложение
EXPOSE 8080

# Запустите приложение
ENTRYPOINT ["java","-jar","app.jar"]