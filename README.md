# Expense Splitter API

A Spring Boot REST API designed to help groups of people split expenses fairly and calculate settlements—modeled after Splitwise.

## Features

- Add, update, and delete expenses
- Support for EQUAL, EXACT, and PERCENTAGE split types
- Automatic creation of persons from names
- Real-time settlement calculation
- JSON-based REST API interface
- Global exception handling
- Support for groups and categories (easily extendable)

## Technologies Used

- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- RESTful APIs

## Setup Instructions

1. **Clone the Repository**
   ```sh
   git clone https://github.com/YOUR_USERNAME/splitwise.git
   cd splitwise
   ```

2. **Configure the Database**

   Edit the file: `src/main/resources/application.properties`

   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/expense_db
   spring.datasource.username=your_postgres_username
   spring.datasource.password=your_postgres_password
   spring.jpa.hibernate.ddl-auto=update
   ```
   > Make sure PostgreSQL is running and replace the username and password with your actual PostgreSQL credentials.

   Create the PostgreSQL database manually:
   ```sql
   CREATE DATABASE expense_db;
   ```

3. **Build and Run the Application**

   Use the included Maven wrapper:
   ```sh
   ./mvnw spring-boot:run
   ```
   The app will start on [http://localhost:8080](http://localhost:8080).

---

## Database Schema and Entity Relationships

This backend models a group expense-splitting system with three core entities:

| Entity               | Table Name             |
|----------------------|-----------------------|
| Person               | people                |
| Expense              | expenses              |
| ExpenseParticipant   | expense_participants  |

### Entity Relationships

#### 1. Person ↔ Expense (One-to-Many)

A person can pay for multiple expenses.

**In `Person` class:**
```java
@OneToMany(mappedBy = "paidBy")
private Set<Expense> expensesPaid;
```

**In `Expense` class:**
```java
@ManyToOne
@JoinColumn(name = "paid_by", nullable = false)
private Person paidBy;
```

#### 2. Expense ↔ ExpenseParticipant (One-to-Many)

Each expense is shared by multiple participants.

**In `Expense` class:**
```java
@OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<ExpenseParticipant> participants;
```

**In `ExpenseParticipant` class:**
```java
@ManyToOne
@JoinColumn(name = "expense_id", nullable = false)
private Expense expense;
```

#### 3. Person ↔ ExpenseParticipant (One-to-Many)

A person can be part of many expenses (as a participant).

**In `Person` class:**
```java
@OneToMany(mappedBy = "person")
private Set<ExpenseParticipant> expenseShares;
```

**In `ExpenseParticipant` class:**
```java
@ManyToOne
@JoinColumn(name = "person_id", nullable = false)
private Person person;
```

---

## API Exploration with Postman

A complete Postman collection is available to help you test and understand all API endpoints, including sample requests, responses, and documentation for each route.

### Resources:

- **Postman Collection:** [splitwise-collection.postman_collection.json](https://gist.github.com/Abheeee0001/d33f4501ee2b6a9b56b9f4a4b4ebd87b#file-splitwise-collection-postman_collection-json)
- **Postman Environment:** [splitwise_environment.postman_environment.json](https://gist.github.com/Abheeee0001/d33f4501ee2b6a9b56b9f4a4b4ebd87b#file-splitwise_environment-postman_environment-json)

### How to Use the Postman Collection

1. **Download the Collection and Environment**
   - Download both `splitwise-collection.postman_collection.json` and `splitwise_environment.postman_environment.json` from the provided [gist link](https://gist.github.com/Abheeee0001/d33f4501ee2b6a9b56b9f4a4b4ebd87b).

2. **Import into Postman**
   - Open Postman.
   - Click `Import` (top-left corner) and select the downloaded collection file.
   - Import the environment file similarly via the `Environments` tab.

3. **Select the Environment**
   - After importing, select the `splitwise_environment` from the environment dropdown in the top-right corner of Postman.

4. **Explore the Endpoints**
   - Expand the `splitwise` collection in Postman.
   - Each request includes:
     - Request URL, method, and headers
     - Example request bodies (where applicable)
     - Example successful and error responses
     - In-line documentation describing the endpoint's purpose and usage

   - You can edit parameters and body values to test different scenarios.

5. **Read API Documentation**
   - Open any request in the collection to see detailed notes and descriptions.
   - Each endpoint documents:
     - Purpose
     - Required and optional parameters
     - Sample inputs and outputs

6. **Test Your Setup**
   - Ensure your Spring Boot server is running (`http://localhost:8080` by default).
   - Click "Send" on any request in the collection to interact with your running API instance.

