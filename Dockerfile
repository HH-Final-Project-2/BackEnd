FROM azul/zulu-openjdk-alpine:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ADD ["C:\Users\jungi\weekfinal\BackEnd\src\main\resources", "/var/lib/docker"]
ENTRYPOINT ["java","-jar","/app.jar"]

