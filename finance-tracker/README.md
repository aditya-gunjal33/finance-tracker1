# 💰 Finance Tracker

A simple, fully functional **Finance Tracker** web application built with **Java 17**, **Spring Boot**, and **Thymeleaf** — created as a DevOps practical project.

---

## ✨ Features

| Feature | Details |
|---|---|
| **Dashboard** | Cards showing Total Income, Total Expenses, and Balance |
| **Add Transaction** | Form with title, amount, type (Income/Expense), and category |
| **Transaction History** | Colour-coded table with all entries |
| **Delete Transaction** | Remove individual records |
| **Validation** | Server-side (Bean Validation) + client-side (JavaScript) |
| **Health Endpoint** | `GET /health` → `{"status":"UP"}` |
| **H2 Console** | `GET /h2-console` for DB inspection during demo |

---

## 🛠 Technology Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring MVC, Spring Data JPA
- **Database**: H2 in-memory (no setup required)
- **Templates**: Thymeleaf
- **Frontend**: HTML5, Vanilla CSS, Vanilla JavaScript
- **Tests**: JUnit 5, Mockito, Selenium WebDriver 4 + WebDriverManager
- **Build**: Apache Maven
- **Container**: Docker (Eclipse Temurin 17, multi-stage build)
- **CI/CD**: Jenkins (declarative pipeline)

---

## 🚀 Running Locally

### Prerequisites
- Java 17 JDK
- Apache Maven 3.8+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/<YOUR_USERNAME>/finance-tracker.git
cd finance-tracker

# 2. Build & run
mvn spring-boot:run

# 3. Open in browser
# http://localhost:8080
```

The application uses an **in-memory H2 database** — no database installation needed. Data resets on restart.

---

## 🧪 Running Tests

### Unit + Integration Tests (JUnit)
```bash
mvn test
```
Reports are saved to `target/surefire-reports/`.

### Selenium End-to-End Tests
> Requires **Google Chrome** installed. WebDriverManager auto-downloads ChromeDriver.
```bash
# Ensure the app is NOT already running on port 8080 (Failsafe starts it automatically)
mvn verify
```
Reports are saved to `target/failsafe-reports/`.

---

## 🐳 Docker

### Build Image
```bash
docker build -t finance-tracker:latest .
```

### Run Container
```bash
docker run -d -p 8080:8080 --name finance-tracker finance-tracker:latest
```

Open: [http://localhost:8080](http://localhost:8080)

### Stop & Remove Container
```bash
docker stop finance-tracker
docker rm finance-tracker
```

---

## 🔄 Jenkins CI/CD Pipeline

See [`Jenkinsfile`](./Jenkinsfile) for the full declarative pipeline.

### Pipeline Stages
1. **Checkout** — pulls source from GitHub
2. **Build** — `mvn clean compile`
3. **Unit Tests** — `mvn test` + JUnit report
4. **Package** — produces executable JAR
5. **Docker Build** — multi-stage Docker image
6. **Docker Push** — pushes to Docker Hub
7. **Deploy** — runs container on the Jenkins host
8. **Selenium Tests** — `mvn verify` end-to-end

---

## 📁 Project Structure

```
finance-tracker/
├── Dockerfile
├── Jenkinsfile
├── README.md
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/financetracker/
    │   │   ├── FinanceTrackerApplication.java
    │   │   ├── controller/TransactionController.java
    │   │   ├── model/Transaction.java
    │   │   ├── repository/TransactionRepository.java
    │   │   └── service/TransactionService.java
    │   └── resources/
    │       ├── application.properties
    │       ├── static/css/style.css
    │       ├── static/js/app.js
    │       └── templates/index.html
    └── test/
        └── java/com/financetracker/
            ├── SeleniumTest.java
            ├── controller/TransactionControllerTest.java
            └── service/TransactionServiceTest.java
```

---

## 📜 License

MIT — free to use for educational purposes.
