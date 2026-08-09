![CI](https://github.com/BBohdann/codeRecognizer/actions/workflows/ci.yml/badge.svg)
![Coverage](badges/jacoco.svg)
 
# CodeRecognizer
 
CodeRecognizer is a Java application for recognizing and processing barcodes and QR codes.
The project was built as a pet project to practice backend development with Spring Boot, image processing with ZXing, and REST API design.
 
It allows scanning images, validating formats, and handling decoded data, making it useful for automation and data management tasks.
 
---
 
## Features
 
- **Barcode & QR recognition** — detect and decode multiple code types
- **Image validation** — ensure correct format before processing
- **Data decryption** — decode and interpret recognized codes
- **Modular services** — structured architecture for easy extension
- **Automated testing** — unit tests run automatically in CI, with coverage tracked via JaCoCo
- **Containerized** — runs via Docker Compose with a bundled PostgreSQL instance, no manual setup required
---
 
## Tech Stack
 
- **Backend:** Java, Spring Boot
- **Image Processing:** ZXing (Zebra Crossing)
- **Database:** PostgreSQL
- **Migrations:** Flyway
- **Testing:** JUnit, Mockito
- **Containerization:** Docker, Docker Compose
- **CI/CD:** GitHub Actions
- **Build Tool:** Gradle
- **API Docs:** Swagger / OpenAPI
---
 
## Getting Started
 
**Run with Docker Compose (recommended)** — starts the app and its PostgreSQL database together:
 
```bash
git clone https://github.com/BBohdann/codeRecognizer.git
cd codeRecognizer
cp .env.example .env
docker-compose up --build
```
 
**Or run locally with Gradle** (requires your own PostgreSQL instance and a `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` environment set):
 
```bash
./gradlew bootRun
```
 
The application will start at:
```
http://localhost:8080
```
 
Swagger UI (API documentation) available at:
```
http://localhost:8080/swagger-ui/index.html
```
 
---
 
## Usage
 
1. Upload an image with a barcode or QR code
2. The service validates the format
3. Recognized data is returned via REST API
---
 
# About the Project
 
This project was developed as part of self-study to strengthen Java backend skills.
It focuses on practical application of image recognition, API design, and service modularity, preparing for real-world backend tasks.
 
# License
 
This project is open-source and available under the MIT License.
