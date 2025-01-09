FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","/application.jar"]