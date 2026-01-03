# ============================
# Runtime image — Java 21 LTS
# ============================
FROM eclipse-temurin:21-jre-jammy

# Cria usuário não-root
RUN useradd -ms /bin/bash appuser

WORKDIR /app

ARG JAR_FILE=target/virtual-card-platform.jar

COPY ${JAR_FILE} app.jar

RUN chown appuser:appuser app.jar

USER appuser

# JVM preparada para containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
