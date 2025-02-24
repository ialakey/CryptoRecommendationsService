FROM openjdk:17
WORKDIR /app
COPY build/libs/CryptoRecommendationsService-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/data/ /app/data/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
