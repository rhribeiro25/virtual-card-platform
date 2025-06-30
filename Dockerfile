FROM eclipse-temurin:17-jdk-jammy

# Define o JAR a ser copiado
ARG JAR_FILE=target/virtual-card-platform-api.jar

# Copia o JAR para o nome fixo que o ENTRYPOINT espera
COPY ${JAR_FILE} virtual-card-platform-api.jar

# Executa o JAR corretamente
ENTRYPOINT ["java", "-jar", "/virtual-card-platform-api.jar"]
