[README in pt-br :brazil:](https://github.com/neemiassgc/contact-manager-api/)
# Contact Manager API
A multi-user, tested and documented Rest API for managing contacts for the purpose of demonstrating CRUD operations.

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
* Spring Boot + Spring Security integrated with Oauth2 acting as a resource server
* Spring Data JPA + Hibernate
* Postgres on cloud provided by Supabase
* Multiuser
* Documented with Swagger ([link para documentação](https://mythic-guild-431115-g2.uc.r.appspot.com/swagger-ui/index.html))
* Docker image on [Docker Hub](https://hub.docker.com/r/k4mek/contact-manager)
* Deployed on Google Cloud App Engine

### Database diagram :page_facing_up:
<img src="https://static-10.s3.sa-east-1.amazonaws.com/contact-manager-api/contact-manager-schema.png" width="960" height="540"/>

### Demo :fire:
Visit the project [Contact Manager UI](https://github.com/neemiassgc/contact-manager-ui) which creates a Web UI to use this API.

### Test :test_tube:
To run the tests, a Postgres database is required. To do this, with the repository already cloned, go to the root of the project and run the following docker command:
```bash
docker compose -f docker/postgres.yml up -d
```
Then use Gradle to run the tests:
```bash
./gradlew test
```

### Run locally using Docker :cyclone:
Just use the following command ```docker compose``` in the root of the project to have the application running:
```bash
docker compose -f docker/app.yml up -d
```
After that, the API can be accessed at http://localhost:8080. Note: A JWT token is required to access the API.

### License :memo:
This project is under Mit License
<img src="https://mythic-guild-431115-g2.uc.r.appspot.com/files/swagger-badge.svg" width="0" height="0"/>