FROM docker.io/amazoncorretto:17-alpine3.19

WORKDIR /app

COPY build/libs/contact-manager-api-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "./app.jar" ]