FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY backend /app/backend

RUN javac -d /app backend/*.java

CMD ["java", "-cp", "/app", "backend.Main"]