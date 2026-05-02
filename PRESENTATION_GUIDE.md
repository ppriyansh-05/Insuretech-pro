# InsureTech Pro - Presentation Guide

## 1. Opening Introduction

- Good morning/afternoon ma'am/sir.
- My project name is **InsureTech Pro - Insurance Management System**.
- It is built using **Java, JavaFX, JDBC, Maven, and MySQL**.
- It manages real insurance work: agents, customers, policies, premium payments, and claims.
- It has three roles: **Admin, Agent, and Customer**.
- It includes duplicate prevention, delete/deactivate actions, a richer dashboard, and larger demo data.

## 2. Technologies Used

- **Java 17**: Main programming language.
- **JavaFX**: Desktop GUI with login, dashboard, tables, charts, and forms.
- **MySQL**: Permanent database storage.
- **JDBC**: Connects Java code with MySQL.
- **Maven**: Builds the project and manages dependencies.
- **OOP Concepts**: Encapsulation, inheritance, abstraction, overriding, and exception handling.

## 3. Folder And File Meaning

### Root Project Folder

- `pom.xml`: Maven configuration, Java version, dependencies, and plugins.
- `PROJECT_GUIDE.md`: Main project guide with features, roles, commands, and submission notes.
- `PRESENTATION_GUIDE.md`: This presentation and viva cheat sheet.
- `REPORT.docx`: Project report document.
- `JAVA(insurance).pptx`: PowerPoint presentation.
- `target/`: Compiled output and final JAR after packaging.
- `src/`: Main source code and resources.

### `src/main/java/com/insuretechpro`

- `model/`: Data classes like `Customer`, `Agent`, `Policy`, `Claim`, `Payment`, and `User`.
- `repository/`: JDBC classes for saving, loading, updating, deleting, and status changes.
- `database/`: MySQL connection and database initialization classes.
- `ui/`: JavaFX GUI application.
- `exception/`: Custom exception classes for clean error messages.
- `service/`: Business logic classes.
- `main/`: Console demo classes.
- `util/`: Helper classes.

### `src/main/resources`

- `database.properties`: MySQL URL, username, and password.
- `db/schema.sql`: Creates tables and inserts clean demo data.
- `styles/insuretech.css`: GUI colors, cards, tables, and button styling.

## 4. Important Java Files

- `InsureTechProApp.java`: Main JavaFX app with login, dashboard, role screens, and buttons.
- `DatabaseConnection.java`: Creates the MySQL connection.
- `DatabaseInitializer.java`: Runs `schema.sql` and resets demo data.
- `UserRepository.java`: Login users, activate/deactivate, and delete.
- `AgentRepository.java`: Agent save, load, status update, and delete.
- `CustomerRepository.java`: Customer save, load, status update, and delete.
- `PolicyRepository.java`: Saves life, health, vehicle policies and deletes linked records.
- `ClaimRepository.java`: Saves, updates, and deletes claims.
- `PaymentRepository.java`: Saves, updates, and deletes payments.
- `DashboardRepository.java`: Dashboard totals and chart data.
- `Policy.java`: Abstract parent class.
- `LifePolicy.java`, `HealthPolicy.java`, `VehiclePolicy.java`: Child classes showing inheritance.

## 5. User Roles To Explain

### Admin

- Full control of users, agents, customers, policies, claims, payments, and reports.
- Can create, activate, deactivate, and delete login users.
- Can activate/deactivate agents and customers.
- Can issue, cancel, and delete policies.
- Can approve, reject, cancel, settle, and delete claims.
- Can delete payment records.

### Agent

- Sees only assigned customers, policies, claims, and payments.
- Can add customers under their own agent ID.
- Can activate/deactivate assigned customers.
- Can issue and cancel assigned policies.
- Can process or reject assigned claims.

### Customer

- Sees only their own policies, claims, and payments.
- Can submit a claim for their own policy.
- Can record premium payment.
- Can cancel their own claim.

## 6. Demo Logins

```text
Admin:     admin / admin123
Agent 1:   agent / agent123
Agent 2:   agent2 / agent2123
Agent 3:   agent3 / agent3123
Customer:  customer / customer123
Customer:  priya / priya123
Customer:  imran / imran123
Customer:  meera / meera123
Customer:  vikram / vikram123
Customer:  ananya / ananya123
```

## 7. Step By Step Run Guide

### Step 1: Open Project Folder

```powershell
cd "C:\Users\ppriy\OneDrive\Desktop\semester 4\java\InsureTech Pro - Insurance Management System"
```

If the above path fails, open the folder manually in File Explorer, click the address bar, type `powershell`, and press Enter.

### Step 2: Start MySQL

- Make sure MySQL server is running.
- Check `src/main/resources/database.properties`.
- Confirm `db.username` and `db.password` match your MySQL login.

### Step 3: Reset/Create Database

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' exec:java "-Dexec.mainClass=com.insuretechpro.database.DatabaseInitializer"
```

Expected result:

```text
Database and tables created successfully.
```

### Step 4: Compile

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' compile
```

### Step 5: Run App

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' javafx:run
```

### Step 6: Package

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' package
```

## 8. Presentation Flow

- Show project folder.
- Open `pom.xml` and explain Maven dependencies.
- Open `database.properties` and explain database settings.
- Open `schema.sql` and explain tables and demo data.
- Open `Policy.java` and explain abstraction.
- Open child policy files and explain inheritance.
- Open `repository` package and explain JDBC with `PreparedStatement`.
- Open `InsureTechProApp.java` and explain GUI and role checks.
- Run database initializer.
- Run JavaFX app.
- Login as admin, agent, and customer.

## 9. Demo Script

- Login as `admin / admin123`.
- Show dashboard cards, revenue, policy chart, and recent claims.
- Open Users and show activate/deactivate/delete.
- Open Agents and Customers and show status/delete actions.
- Open Policies and show life, health, and vehicle policies.
- Open Claims and show approve, process, reject, settle, cancel, and delete.
- Open Payments and show premium records.
- Logout.
- Login as `agent / agent123`.
- Show only assigned customers, policies, claims, and payments.
- Logout.
- Login as `customer / customer123`.
- Show only own policies, claims, and payments.

## 10. Viva Answers

- **What is this project?** Insurance management system for agents, customers, policies, payments, and claims.
- **Why JavaFX?** To build a desktop GUI with forms, tables, charts, and buttons.
- **Why MySQL?** To store permanent relational data with primary keys and foreign keys.
- **What prevents duplicate records?** Primary keys, unique constraints, and Java validation checks.
- **What prevents same customer ID?** `customer_id` is an auto-increment primary key, so MySQL creates a unique ID for every customer.
- **Where is inheritance used?** `Policy` is parent; `LifePolicy`, `HealthPolicy`, and `VehiclePolicy` are child classes.
- **Where is abstraction used?** `Policy` is abstract.
- **Where is encapsulation used?** Private fields with public getters and setters.
- **Where is overriding used?** Each policy type overrides `calculatePremium()`.
- **How is SQL injection prevented?** Repository classes use `PreparedStatement`.
- **How is role access handled?** Login user has `role` and `reference_id`.

## 11. Common Problems

- Maven command fails: use the full Maven path from this guide.
- Login fails: run `DatabaseInitializer`.
- Database fails: start MySQL and check `database.properties`.
- JavaFX does not open: confirm JDK 17 and Maven are installed.
- Old/duplicate demo data appears: run `DatabaseInitializer` again.

## 12. Final Checklist

- `mvn compile` passes.
- `mvn package` passes.
- Database initializer passes.
- JavaFX app opens.
- Admin, agent, and customer login work.
- Dashboard has data.
- Delete/deactivate buttons work according to role.
