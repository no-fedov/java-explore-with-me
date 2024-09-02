FROM amazoncorretto:21
COPY target/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/app.jar"]
EXPOSE 8080