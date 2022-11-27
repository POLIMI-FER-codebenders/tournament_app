# Build step
FROM maven:3.8-openjdk-17 AS build
RUN mkdir /Tournament_app/
WORKDIR /Tournament_app/
COPY ./backend_ta/pom.xml .
COPY ./backend_ta/src ./src/
RUN mvn clean package -DskipTests

# Release step
FROM tomcat:9.0-jdk17-openjdk AS release
COPY --from=build /Tournament_app/target/tournament_app-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
