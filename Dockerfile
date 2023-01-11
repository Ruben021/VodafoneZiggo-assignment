FROM amazoncorretto:17-alpine as jre

RUN apk add --no-cache binutils

RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules java.base,java.desktop,java.instrument,java.logging,java.management,java.management.rmi,java.naming,java.net.http,java.prefs,java.rmi,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,java.xml.crypto,jdk.unsupported,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /customjre

FROM amazoncorretto:17-alpine as dependencies

WORKDIR /opt/app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

FROM dependencies as builder

WORKDIR /opt/app

COPY ./src ./src
RUN ./mvnw clean package

FROM alpine:latest
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=jre /customjre $JAVA_HOME

RUN mkdir /app

COPY --from=builder /opt/app/target/*.jar /app/app.jar

WORKDIR /app

EXPOSE 8080
ENTRYPOINT [ "/jre/bin/java", "-jar", "/app/app.jar" ]