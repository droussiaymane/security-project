FROM 147642600964.dkr.ecr.ap-south-1.amazonaws.com/azul/zulu-openjdk-alpine:18.0.2-kgroup as builder
ENV TZ="Asia/Kolkata"
ARG JAR_FILE=libs
COPY libs/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM 147642600964.dkr.ecr.ap-south-1.amazonaws.com/azul/zulu-openjdk-alpine:18.0.2-jre-headless-kgroup
ENV TZ="Asia/Kolkata"
RUN cp /usr/lib/jvm/zulu18-ca/lib/security/cacerts /tmp/kafka.client.truststore.jks
COPY --from=builder /home/appuser/dependencies/ ./
COPY --from=builder /home/appuser/spring-boot-loader/ ./
COPY --from=builder /home/appuser/snapshot-dependencies/ ./
COPY --from=builder /home/appuser/application/ ./
EXPOSE 12291
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]