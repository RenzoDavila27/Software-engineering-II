FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/gimnasio-0.0.1-SNAPSHOT.jar 
COPY ${JAR_FILE} gimnasio-0.0.1-SNAPSHOT.jar 
EXPOSE 8080
ENTRYPOINT ["java","-jar","gimnasio-0.0.1-SNAPSHOT.jar"]