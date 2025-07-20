# Telstra Starter Repo :bird:

This repo has everything you need to get started on the Telstra program!

## What was implemented

1. **Project Setup**
   - Forked and cloned the starter repository.
   - Opened the project in a Java 11 and Maven compatible IDE.

2. **Actuator Microservice**
   - The provided `SimCardActuator.jar` is run locally on port 8444 to handle SIM activation requests.

3. **Spring Boot Microservice**
   - Implemented a REST controller with the following endpoints:
     - `POST /activate`: Accepts JSON payloads with `iccid` and `customerEmail`, relays the ICCID to the actuator microservice, and returns whether activation was successful.
     - `GET /activations`: Returns all activation attempts stored in the database as JSON.
   - For each activation attempt, the service:
     - Forwards the ICCID to the actuator service at `http://localhost:8444/actuate`.
     - Receives and logs the activation result.
     - Persists the activation attempt (ICCID, customer email, result, timestamp) to an H2 database using Spring Data JPA.

4. **Persistence**
   - Created a JPA entity and repository to store all activation attempts for future retrieval.

5. **How to Run**
   - Start the actuator service:
     ```sh
     java -jar services/SimCardActuator.jar
     ```
   - Start the Spring Boot application:
     ```sh
     ./mvnw spring-boot:run
     ```
   - Test the endpoints using curl or Postman:
     - Activate a SIM:
       ```sh
       curl -X POST http://localhost:8080/activate \
         -H "Content-Type: application/json" \
         -d '{"iccid":"1234567890123456789","customerEmail":"test@example.com"}'
       ```
     - View all activations:
       ```sh
       curl http://localhost:8080/activations
       ```

6. **Repository**
   - All changes have been committed and pushed to: [https://github.com/srikanth10-web/Telstra-AU-.git](https://github.com/srikanth10-web/Telstra-AU-.git)
