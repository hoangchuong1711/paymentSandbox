FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/ch07cart.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 10000
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-10000}\\\"/\" \"$CATALINA_HOME/conf/server.xml\" && catalina.sh run"]
