# InsureTech Pro - Project Guide

## Project Summary

InsureTech Pro is a Java and MySQL insurance management system with a JavaFX desktop interface. It supports real insurance workflows: customer onboarding, agent assignment, policy issue, premium payment, claim submission, claim review, approval, rejection, settlement, and role-based login.

## Main Features

- Java OOP model classes: `Customer`, `Agent`, `Claim`, `Payment`, `User`, and abstract `Policy`.
- Policy inheritance: `LifePolicy`, `HealthPolicy`, and `VehiclePolicy`.
- MySQL database with normalized tables, foreign keys, validation checks, and demo data.
- JDBC repository layer using `PreparedStatement` for safe database access.
- JavaFX GUI with login, dashboard, tables, charts, add forms, and role-based navigation.
- Multi-user login system for `ADMIN`, `AGENT`, and `CUSTOMER`.
- Admin Users screen to create and activate/deactivate login accounts.
- Delete/deactivate actions for users, agents, customers, policies, claims, and payments.
- Strong duplicate checks for customer email, phone, code, policy number, claim number, and payment reference.
- Clean management dashboard with customers, policies, claims, payments, revenue, role summary, recent claims, and chart.
- Role-scoped dashboard, policies, claims, and payments.

## Demo Logins

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

## User Roles

### Admin

- Has full access to agents, users, customers, policies, claims, payments, reports, and settings.
- Can create login users for admins, agents, and customers.
- Can activate or deactivate login users.
- Can delete users, claims, payments, and policies when needed.
- Can add agents and customers.
- Can activate/deactivate agents and customers.
- Can issue policies for any active customer and active agent.
- Can view and manage all claims.
- Can approve, reject, cancel, and settle claims.

### Agent

- Sees only customers assigned to the logged-in agent ID.
- Can add customers under their own agent ID.
- Can issue policies only for assigned customers.
- Can view policies, claims, and payments related to their assigned policies.
- Can activate/deactivate assigned customers and cancel/activate assigned policies.
- Can mark assigned claims as processing or rejected.
- Cannot access another agent's customers or policies.

### Customer

- Sees only their own policies, claims, and payments.
- Can submit claims only for policies belonging to their own customer ID.
- Can record payments only for their own policies.
- Can cancel their own claims.
- Cannot view other customers, agents, or admin screens.

## Database Setup

Edit `src/main/resources/database.properties` if your MySQL username or password is different.

Create or refresh the database tables and demo records. This resets the demo database so the presentation starts clean:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' exec:java "-Dexec.mainClass=com.insuretechpro.database.DatabaseInitializer"
```

Test the JDBC connection:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' exec:java "-Dexec.mainClass=com.insuretechpro.database.DatabaseConnectionTest"
```

## Run Commands

Compile:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' compile
```

Package:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' package
```

Run JavaFX GUI:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' javafx:run
```

Run database console demo:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' exec:java "-Dexec.mainClass=com.insuretechpro.main.DatabaseDemoMain"
```

## Deployment And Submission

Submit the full Maven project folder with these important files:

- `pom.xml`
- `src/main/java`
- `src/main/resources`
- `PROJECT_GUIDE.md`
- `REPORT.docx`
- `JAVA(insurance).pptx`

Before submission, run:

```powershell
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' compile
& 'C:\Program Files\Apache\Maven\apache-maven-3.9.15\bin\mvn.cmd' package
```

The packaged output is created in `target/`. For checking on another computer, install JDK 17, Maven, and MySQL, update `database.properties`, run the database initializer, then run `javafx:run`.

## Viva Points

- `Policy` is abstract because a general policy is only a concept.
- `LifePolicy`, `HealthPolicy`, and `VehiclePolicy` show inheritance.
- `calculatePremium()` and `displayPolicyDetails()` show method overriding.
- Encapsulation is shown by private fields with getters and setters.
- `PolicyRepository` demonstrates storing inherited classes using parent and child tables.
- Foreign keys connect agents, customers, policies, claims, and payments.
- `PreparedStatement` prevents SQL injection in login and database operations.
- Role-based access control is handled after login using the `User` role and `reference_id`.
- Admin, agent, and customer dashboards show data according to the logged-in role.
