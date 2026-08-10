![CI](https://github.com/BBohdann/codeRecognizer/actions/workflows/ci.yml/badge.svg)
![Coverage](badges/jacoco.svg)
 
# CodeRecognizer
 
CodeRecognizer is a REST API service for recognizing and decoding barcodes and QR codes from uploaded images, built with Java and Spring Boot. It validates image formats, extracts and interprets the encoded data via ZXing, and returns structured results — useful for automation and data management tasks.
 
---
 
## Features
 
- **Barcode & QR recognition** — detect and decode multiple code types
- **Image validation** — ensure correct format before processing
- **Data decoding** — decode and interpret recognized codes
- **Modular services** — structured architecture for easy extension
- **3 REST Endpoints** — documented and testable via Swagger UI
- **Automated testing** — unit tests run automatically in CI, with coverage tracked via JaCoCo
- **Containerized** — runs via Docker Compose with a bundled PostgreSQL instance, no manual setup required
---
 
## Tech Stack
 
- **Backend:** Java, Spring Boot
- **Image Processing:** ZXing (Zebra Crossing)
- **Database:** PostgreSQL
- **Migrations:** Flyway
- **Mapping:** MapStruct
- **Boilerplate reduction:** Lombok
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
 
## About the Project
 
Developed as a self-study project to practice production-style backend patterns — REST API design, image processing integration, automated testing, and containerized deployment — beyond typical CRUD tutorials.
 
## License
 
This project is open-source and available under the MIT License.
