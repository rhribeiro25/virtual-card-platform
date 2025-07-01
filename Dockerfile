FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/virtual-card-platform.jar

COPY ${JAR_FILE} virtual-card-platform.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/virtual-card-platform.jar"]

