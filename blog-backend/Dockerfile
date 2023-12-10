FROM maven:3.9.5-eclipse-temurin-17 as builder
WORKDIR /app
COPY ./pom.xml .
RUN mvn clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
COPY ./src ./src
RUN mvn clean package


FROM eclipse-temurin:17-jammy
WORKDIR /app
COPY --from=builder /app/target/blog-backend-0.0.1.jar .
EXPOSE 8080
CMD ["java", "-jar","blog-backend-0.0.1.jar"]



# # Usa una imagen base de Maven para compilar la aplicación
# FROM maven:3.9.5-eclipse-temurin-17 AS build
# # Copia el archivo POM y descarga las dependencias
# WORKDIR /app
# COPY ./pom.xml /app
# RUN mvn clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# # Copia el resto de los archivos y realiza la compilación
# COPY ./src ./src
# RUN mvn clean package -DskipTests

# # # Crea una imagen ligera de OpenJDK para la ejecución
# # FROM openjdk:11-jre-slim
# # # Copia el archivo JAR compilado desde la fase de compilación
# # COPY --from=build /app/target/tu-aplicacion.jar /app/app.jar
# # # Establece el directorio de trabajo
# # WORKDIR /app
# # # Expone el puerto en el que la aplicación se ejecuta
# # EXPOSE 8080
# # # Comando para ejecutar la aplicación Spring Boot
# # CMD ["java", "-jar", "app.jar"]
