FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY api-ms-customer-identity-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
USER nobody
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q --spider http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]