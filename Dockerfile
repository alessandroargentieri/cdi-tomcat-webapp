FROM maven:3.8.3-jdk-11-slim as build
WORKDIR /tmp/context
COPY . .
RUN mvn clean package

FROM tomcat:9.0-jre11-temurin-focal
WORKDIR $CATALINA_HOME
COPY --from=build /tmp/context/target/cdi-project-*.war ./webapps/cdi.war

EXPOSE 8080/tcp
