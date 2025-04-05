FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY api-ms-financial-movement-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
USER nobody
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget -q --spider http://localhost:9090/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]