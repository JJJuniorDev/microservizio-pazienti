# Usa OpenJDK 17
FROM openjdk:17-jdk-slim

# Installa Maven
RUN apt-get update && apt-get install -y maven

# Imposta la directory di lavoro
WORKDIR /app

# Copia il codice sorgente
COPY . .

# Compila il progetto con Maven (senza dipendere dalla tua macchina)
RUN mvn clean package

# Espone la porta 8082
EXPOSE 8082

# Avvia l'applicazione
CMD ["java", "-jar", "target/sicurezza-0.0.1-SNAPSHOT.jar"]
