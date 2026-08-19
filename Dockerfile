# Stage de build
FROM azul/zulu-openjdk:25-jdk AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean install -DskipTests

# Stage de execução
FROM azul/zulu-openjdk:25-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


#FROM azul/zulu-openjdk:25-jre
#WORKDIR /app
#COPY target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]