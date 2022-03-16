FROM openjdk:17-jdk-alpine
COPY target/DocHauserSrv-1.0.0.jar DocHauserSrv-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/DocHauserSrv-1.0.0.jar"]

