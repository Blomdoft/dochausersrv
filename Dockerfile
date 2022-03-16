FROM openjdk:17-jdk-alpine

RUN groupadd -g 1001 scanner \
        && useradd -rm -d /home/scanner -s /bin/bash -g 1001 -u 1001 scanner

WORKDIR /home/scanner

# Volumes for mounts
VOLUME /home/scanner/archive
VOLUME /home/scanner/scanner

EXPOSE 8080

COPY target/DocHauserSrv-1.0.0.jar DocHauserSrv-1.0.0.jar
USER scanner:scanner
ENTRYPOINT ["java", "-jar", "/DocHauserSrv-1.0.0.jar"]

