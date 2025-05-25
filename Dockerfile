# Use uma imagem base leve com Java 17
FROM eclipse-temurin:17-jdk-alpine

# Defina o diretório de trabalho
WORKDIR /app

# Crie um usuário e grupo não-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copie o JAR da aplicação para o diretório de trabalho no container
# Certifique-se que o JAR está na pasta 'target' do seu projeto local antes de construir a imagem
COPY target/mottu-track-api1-1.0.0.jar app.jar

# Mude a propriedade do arquivo JAR para o usuário não-root
RUN chown appuser:appgroup app.jar

# Mude para o usuário não-root
USER appuser

# Exponha a porta que a aplicação usa
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

