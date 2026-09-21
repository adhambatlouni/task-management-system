FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

COPY src/ src/
RUN chmod +x mvnw && ./mvnw --no-transfer-progress package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
