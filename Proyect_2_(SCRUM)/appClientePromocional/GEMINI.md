# Project Overview

This is a car rental web application built with Java and the Spring Boot framework. It uses Maven for project management and Thymeleaf for server-side rendering of HTML templates. The application appears to be a promotional website for a car rental company called "ROYAL CARS" or "Cental".

# Building and Running

To build and run the project, you can use the following Maven command:

```bash
./mvnw spring-boot:run
```

This will start the application on the default port (usually 8080). You can then access it in your web browser at `http://localhost:8080`.

# Development Conventions

*   **Java 17:** The project is configured to use Java 17.
*   **Spring Boot:** The application follows standard Spring Boot conventions.
*   **Thymeleaf:** HTML templates are located in the `src/main/resources/templates` directory.
*   **Static Assets:** CSS, JavaScript, and images are located in the `src/main/resources/static` directory.
*   **Testing:** The project includes a test class in `src/test/java/com/fioritech/car/CarApplicationTests.java`. To run the tests, you can use the following Maven command:

```bash
./mvnw test
```
