FROM eclipse-temurin:17-jre
WORKDIR /app
COPY server/build/libs/server-all.jar app.jar
EXPOSE 1226
ENTRYPOINT ["java", "-jar", "app.jar"]
