FROM openjdk:15-slim-buster
WORKDIR /app
COPY simple-kafka-setup-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT exec java -jar /app/app.jar $@