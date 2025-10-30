FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/biblioteca-0.0.1-SNAPSHOT.jar 
COPY ${JAR_FILE} biblioteca-0.0.1-SNAPSHOT.jar 
EXPOSE 8080
ENTRYPOINT ["java","-jar","biblioteca-0.0.1-SNAPSHOT.jar"]