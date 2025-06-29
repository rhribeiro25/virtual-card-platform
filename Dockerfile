FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/virtual-card-platform-api.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
