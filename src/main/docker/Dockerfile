FROM java:8-jdk
MAINTAINER Thorsten Deelmann

VOLUME /tmp
ADD /maven/spring-boot-health-test-0.0.1-SNAPSHOT.jar spring-boot-health-test.jar
RUN sh -c 'touch /spring-boot-health-test-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java", "-Xmx512m", "-XX:MaxMetaspaceSize=256m", "-Xss256k", "-Djava.security.egd=file:/dev/./urandom","-jar","/spring-boot-health-test.jar"]
