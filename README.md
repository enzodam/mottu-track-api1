# 🏍️ Mottu Track API

API REST para gestão de motos e filiais desenvolvida para o Challenge 1º SPRINT.


## 📋 Descrição do Projeto
Sistema de rastreamento e gestão de motos compartilhadas, com:

- CRUD completo de motos e filiais
  
- Relacionamento entre entidades
  
- Filtros avançados com paginação
  
- Cache para otimização de performance
  
- Validação de dados e tratamento de erros
  

## 🛠️ Tecnologias

- Java 17
  
- Spring Boot 3.3.2
  
- Spring Data JPA
  
- Oracle Database
  
- Caffeine Cache
  
- Swagger (OpenAPI 3.0)
  

## 👨‍💻 Desenvolvedores

| Nome                          | RM      | GitHub |
|-------------------------------|---------|--------|
| Enzo Dias Alfaia Mendes       | 558438  | [@enzodam](https://github.com/enzodam) |
| Matheus Henrique Germano Reis | 555861  | [@MatheusReis48](https://github.com/MatheusReis48) |
| Luan Dantas dos Santos        | 559004  | [@lds2125](https://github.com/lds2125) |


## 🚀 Executando o Projeto


### Pré-requisitos

- JDK 17 instalado
  
- Maven 3.8+ (opcional para quem usa IDE)
  
- Banco de dados Oracle configurado ou Docker
  

### ▶️ Execução

1. Clone o repositório

git clone https://github.com/enzodam/mottu-track-api1.git


2. Acesse a pasta do projeto
   
cd mottu-track-api1


3. Configuração do Banco de Dados

Edite o arquivo src/main/resources/application.properties:

properties

spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE

spring.datasource.username=SEU_USUARIO

spring.datasource.password=SUA_SENHA


4. Abrir e rodar a API : Via IntelliJ (Recomendado):

Abra o projeto no IntelliJ

Clique no botão ▶️ (Run) ao lado da classe MottuTrackApi1Application

ou

Via Terminal (Maven):

mvn clean install

mvn spring-boot:run

Via Executável:

java -jar target/mottu-track-api1-1.0.0.jar


📚 Documentação da API

Acesse automaticamente após iniciar a aplicação:


Interface Swagger UI: 🌐 http://localhost:8080/swagger-ui.html


Endpoint JSON: 📋 http://localhost:8080/v3/api-docs
