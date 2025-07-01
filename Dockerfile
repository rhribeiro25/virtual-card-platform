FROM eclipse-temurin:17-jdk-jammy

# Define o JAR a ser copiado
ARG JAR_FILE=target/virtual-card-platform.jar

# Copia o JAR para o nome fixo que o ENTRYPOINT espera
COPY ${JAR_FILE} virtual-card-platform.jar

# Executa o JAR corretamente
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/virtual-card-platform.jar"]

