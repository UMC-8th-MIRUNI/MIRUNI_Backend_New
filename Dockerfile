FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]