FROM eclipse-temurin:17-jdk-jammy
COPY ${JAR_FILE} virtual-card-platform-api.jar
ENTRYPOINT ["java","-jar","/virtual-card-platform-api.jar"]
