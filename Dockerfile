FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew buildFatJar --no-daemon -Dorg.gradle.jvmargs="-Xmx512m -XX:MaxMetaspaceSize=256m"

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
