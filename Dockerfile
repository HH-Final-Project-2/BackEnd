FROM azul/zulu-openjdk-alpine:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ADD ["/home/ubuntu/ocr-api-testpj-c2345877fb97.json", "/var/lib/docker"]
ENTRYPOINT ["java","-jar","/app.jar"]

