FROM java:8-jdk
MAINTAINER Thorsten Deelmann

VOLUME /tmp
ADD /maven/kube-springboot-hazelcast-0.0.1-SNAPSHOT.jar kube-springboot-hazelcast.jar
RUN sh -c 'touch /kube-springboot-hazelcast.jar'
ENTRYPOINT ["java", "-Xmx512m", "-XX:MaxMetaspaceSize=256m", "-Xss256k", "-Djava.security.egd=file:/dev/./urandom","-jar","/kube-springboot-hazelcast"]
