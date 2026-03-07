FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

