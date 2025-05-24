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


## 🚀 Como Executar

### Pré-requisitos

- JDK 17
  
- Maven 3.8+
  
- Oracle Database (ou Docker para container Oracle)
  

### Passo a Passo

1. Clone o repositório:
   
   git clone https://github.com/enzodam/mottu-track-api1

  
2. Configure o banco de dados:

Edite o application.properties com suas credenciais Oracle


3. Execute a aplicação:

mvn spring-boot:run


4. Acesse a documentação:

Swagger UI: http://localhost:8080/swagger-ui.html

API Docs: http://localhost:8080/v3/api-docs
