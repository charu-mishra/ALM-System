# ALM System

Asset Liability Management (ALM) System built with **Java 17**, **JavaFX**, **Maven**, **MySQL**, and **JDBC**.

This repository is being developed as a group project to demonstrate:
- Java project structure
- JavaFX UI setup
- MySQL database design
- JDBC database connection
- Model classes for assets and liabilities
- Future CRUD operations, risk analysis, scenario analysis, and reporting

---

## Project Status

### Completed so far
- Java 17 installed and configured
- Maven installed and configured
- JavaFX project created successfully
- Git repository initialized
- MySQL Server installed
- MySQL Workbench installed
- MySQL database created
- JDBC connection tested successfully from Java
- Core model classes created:
  - `Asset`
  - `Liability`
  - `User`
  - `Scenario`

### Next planned work
- Asset DAO and CRUD operations
- Liability DAO and CRUD operations
- JavaFX dashboard UI
- Asset management screen
- Liability management screen
- Risk calculation services
- Scenario analysis
- Reports

---

## Tech Stack

- **Language:** Java 17
- **Build Tool:** Maven
- **UI:** JavaFX
- **Database:** MySQL
- **Database Access:** JDBC
- **Version Control:** Git + GitHub

---

## Folder Structure

```text
ALM_System/
└── alm-system/
    ├── pom.xml
    ├── database.sql
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/
    │       │       └── alm/
    │       │           ├── application/
    │       │           ├── controller/
    │       │           ├── dao/
    │       │           ├── database/
    │       │           ├── model/
    │       │           ├── service/
    │       │           └── util/
    │       └── resources/
    │           ├── css/
    │           ├── fxml/
    │           └── images/
    └── target/
```

---

## Database Setup

The database name is:

```sql
alm_system
```

### Tables created
- `users`
- `assets`
- `liabilities`
- `scenarios`

### Example SQL

```sql
CREATE DATABASE IF NOT EXISTS alm_system;
USE alm_system;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE assets (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_name VARCHAR(100) NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    maturity_date DATE,
    currency VARCHAR(10),
    asset_type VARCHAR(50),
    rate_type VARCHAR(20)
);

CREATE TABLE liabilities (
    liability_id INT PRIMARY KEY AUTO_INCREMENT,
    liability_name VARCHAR(100) NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    maturity_date DATE,
    currency VARCHAR(10),
    liability_type VARCHAR(50),
    rate_type VARCHAR(20)
);

CREATE TABLE scenarios (
    scenario_id INT PRIMARY KEY AUTO_INCREMENT,
    scenario_name VARCHAR(100) NOT NULL,
    interest_change DECIMAL(5,2) NOT NULL,
    created_on DATE
);
```

---

## JavaFX / Maven Run Command

To run the app:

```bash
mvn clean javafx:run
```

If needed, the JavaFX plugin can also be run directly with:

```bash
mvn -U org.openjfx:javafx-maven-plugin:0.0.8:run
```

---

## JDBC Test

A test connection was successfully made from Java to MySQL using:

- `DBConnection.java`
- `TestConnection.java`

The test output confirmed:

```text
Connected to MySQL successfully!
```

---

## Model Classes

### `Asset`
Represents bank assets such as loans and investments.

### `Liability`
Represents bank liabilities such as deposits and borrowings.

### `User`
Represents login/user data.

### `Scenario`
Represents scenario-based analysis inputs such as interest-rate changes.

---

## Team Workflow

Recommended team task split:

- **Member 1:** Project setup, Git, integration
- **Member 2:** Asset module
- **Member 3:** Liability module
- **Member 4:** Risk calculations and scenario analysis
- **Member 5:** Reports and documentation

---

## Notes for Teammates

Before contributing:
1. Pull the latest code from GitHub.
2. Open the project in VS Code.
3. Make sure Java 17, Maven, and MySQL are installed.
4. Verify the project runs using `mvn clean javafx:run`.
5. Keep package names under `com.alm`.

---

## Upcoming Milestones

1. Build Asset DAO and CRUD
2. Build Liability DAO and CRUD
3. Build Dashboard UI
4. Add risk calculations
5. Add scenario analysis
6. Add reports
7. Final integration and testing

---

## GitHub Repository

Repository:
`https://github.com/kibansal/ALM-System.git`
