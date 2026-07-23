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

---

## Tech Stack
- **Backend:** Java, Spring Boot
- **Image Processing:** ZXing (Zebra Crossing)
- **Database:** PostgreSQL
- **Build Tool:** Gradle

---

## Getting Started
Clone and run locally:
```bash
git clone https://github.com/BBohdann/codeRecognizer.git
cd codeRecognizer
```
Build and run with Gradle:
```bash
./gradlew bootRun
```
The application will start at:
```bash
http://localhost:8080
```
Swagger UI (API documentation) available at:
```bash
http://localhost:8080/swagger-ui/index.html
```

## Usage
1. Upload an image with a barcode or QR code
2. The service validates the format
3. Recognized data is returned via REST API

# About the Project
This project was developed as part of self-study to strengthen Java backend skills.
It focuses on practical application of image recognition, API design, and service modularity, preparing for real-world backend tasks.

# License
This project is open-source and available under the MIT License.
