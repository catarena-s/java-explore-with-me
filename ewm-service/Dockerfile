FROM amazoncorretto:11
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY target/*.jar ewm_service_app.jar
ENTRYPOINT ["java","-jar","/ewm_service_app.jar"]