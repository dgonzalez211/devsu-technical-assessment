FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY api-ms-discovery-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
USER nobody
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q --spider http://localhost:8761/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]