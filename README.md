# log-ingestion-analysis-service

A microservice for ingesting and analyzing structured log files via REST APIs.  
Built with Spring Boot and supports in-memory databases.

---

## Features

- Upload structured log files via API
- Store log entries with metadata
- Filter logs by severity level
- Deployable to Kubernetes using Helm

---

## Architecture Overview

[Client] --> [REST API]
               |
               |-- /logs/upload         --> Ingest logs (multipart file)
               |-- /logs/{id}?level=... --> Query logs by document ID and level
               |
            [Spring Boot Service]
               |
            [H2 DB]

## Example Log File
- src/main/resources/sample.log

## What the Project Does

This microservice is designed to:

✅ Accept structured log files in JSON format (one JSON object per line)  
✅ Store the logs and their metadata in a database (H2)  
✅ Allow querying of logs by severity level via REST API  
✅ Support deployment in Kubernetes using Helm  
✅ Provide Docker image for containerized environments


## Technologies Used

- Java 21
- Spring Boot 3.x
- Maven
- Docker
- Kubernetes + Helm
- H2
- GitHub Actions (CI/CD)


## How to Build & Run

### Build Locally

 Make sure Java 21 is installed.

 ```bash
 ./mvnw clean package

 To Run Locally (H2 In-Memory DB)

 ./mvnw spring-boot:run
 ```

 ## Run tests
 - mvn test to run Junit tests.
 - mvn verify to run both Junit and integration tests.
 - mvn failsafe:integration-test failsafe:verify to run only Integration tests.

## Build, run and push Docker image

 docker build -t ghcr.io/<your-username>/log-ingestion-analysis-service:latest .
 docker run -p 8080:8080 log-ingestion-analysis-service
 docker push ghcr.io/<your-username>/log-ingestion-analysis-service:latest

## Install with Helm

 helm upgrade --install log-service ./ -f "${VALUES_FILE}" \
      --namespace "${DEPLOY_ENVIRONMENT}" \
      --set namespace="${DEPLOY_ENVIRONMENT}" \
      --set image.repository="${DOCKER_IMAGE_URL}" \
      --set image.tag="${DOCKER_IMAGE_TAG}" \
      --version "${ARTIFACT_VERSION}" \

 or helm install log-service ./helm/log-ingestion-analysis-service -n test

## CI/CD (GitHub Actions)

- This project includes a CI workflow that:
- Runs on push to main or feature/*
- Builds the project
- Runs tests
- Builds Docker image
- Publishes to GitHub Container Registry (GHCR)

## Monitoring & Observability
- Spring Boot Actuator and Micrometer can be used to expose Prometheus-compatible metrics.
- Metrics and health information can be scraped and visualized using tools like Prometheus and Grafana.
- A simple option to use is to Add Spring Boot Actuator’s liveness and readiness endpoints for Kubernetes probes (/health/liveness, /health/readiness)
- Include config in app.yml to let prometheus scrape metrics
- Add spring-boot-starter-actuator dependency on project to enable actuator.
- Add Prometheus as a data source in Grafana, to create dashboards with metrics (jvm_memory_used_bytes,process_cpu_usage)
