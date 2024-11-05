# Contact Manager API

Uma API Rest multiusuário, testada e documentada para gerenciar contatos com o propósito de demonstrar operações CRUD.

![GitHub commit activity](https://img.shields.io/github/commit-activity/t/neemiassgc/contacts-manager-api)
![Tests](https://img.shields.io/badge/Tests-75-blue)
![Core language](https://img.shields.io/badge/Language-Java-blue)
![Core framework](https://img.shields.io/badge/Framework-Spring%20Boot-6DB33F?logo=spring-boot)
![Core build](https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle)
![Postgres provider](https://img.shields.io/badge/Postgres-Supabase-3FCF8E?logo=supabase)
![Authentication](https://img.shields.io/badge/Authentication-Oauth2-EB5424)
<a href="https://hub.docker.com/r/k4mek/contact-manager" target="_blank">
    <img src="https://img.shields.io/badge/Container-Docker-2496ED?logo=docker"/>
</a>
![Deployment on Cloud](https://img.shields.io/badge/Deployment-Google%20Cloud-4285F4?logo=google%20cloud)
<a href="https://mythic-guild-431115-g2.uc.r.appspot.com/swagger-ui/index.html" target="_blank">
    <img src="https://img.shields.io/badge/Documentation-Swagger-85EA2D?logo=swagger"/>
</a>

### Destaques :sparkles:
* Spring Boot + Spring Security integrado com Oauth2 atuando como servidor de recursos
* Spring Data JPA com Hibernate
* Postgres em nuvem fornecido pelo Supabase
* Multiusuário
* Documentada com Swagger ([link para documentação](https://mythic-guild-431115-g2.uc.r.appspot.com/swagger-ui/index.html))
* Imagem Docker no [Docker Hub](https://hub.docker.com/r/k4mek/contact-manager)
* Implantada no Google Cloud App Engine

### Diagrama do Banco de dados :page_facing_up:
<img src="https://static-10.s3.sa-east-1.amazonaws.com/contact-manager-api/contact-manager-schema.png" width="960" height="540"/>

### Demostração :fire:
visite o projeto [Contact Manager UI](https://github.com/neemiassgc/contacts-manager-ui) que cria uma interface Web para usar esta API.

### Test :test_tube:
Para executar os tests, é necessário um banco de dados Postgres. Para fazer isso, com o repositório já clonado, entre na raiz do projeto e execute o seguinte comando docker:
```bash
docker compose -f docker/postgres.yml up -d
```
Então use Gradle para executar os tests:
```bash
./gradlew test
```

### Execute localmente usando docker :cyclone:
Basta apenas usar o seguinte comando ```docker compose``` na raiz do projeto para ter a aplicação rodando:
```bash
docker compose -f docker/app.yml up -d
```
Depois disso, a API pode ser acessado em http://localhost:8080. Obs: Um token JWT é necessário para acessar a API.

### Licença :memo:
Esse projeto está sob a Mit License
<img src="https://mythic-guild-431115-g2.uc.r.appspot.com/files/swagger-badge.svg" width="0" height="0" alt="swagger badge"/>
