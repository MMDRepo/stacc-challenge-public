# PayForJoy

PayForJoy is a budgeting application built using **Java**, **Spring Boot**, **Maven**, and **SQL**. It provides a robust foundation for managing personal finances with features like account management, transaction tracking, savings goals, and gamification elements.

## Features

### Account Management
- Create, view, update, and delete accounts.
- Supports multiple account types (e.g., Checking, Savings).
- Tracks account balances in real-time.

### Transaction Tracking
- Record and categorize income and expenses.
- Supports multiple transaction types (e.g., Income, Expense).
- Provides detailed transaction history for each account.

### Savings Goals
- Set financial goals with target amounts and deadlines.
- Track progress toward achieving goals.
- Includes active and completed goal statuses.

### Dashboard
- Provides an overview of financial status.
- Displays charts and visualizations for better insights.

### Reward System
- Gamification elements to encourage saving.
- Rewards for milestones like creating goals or achieving savings targets.
- Tracks claimed and unclaimed rewards.

### Error Handling
- Comprehensive exception management for a seamless user experience.
- Logs errors and warnings for debugging and monitoring.

### Logging
- Detailed application logging using SLF4J.
- Includes query logging for database operations (configurable).

## Technical Details

### Backend
- **Spring Boot**: Provides the core framework for the application.
- **H2 Database**: In-memory database for development and testing.
- **JPA**: Manages database interactions with repositories for entities like `User`, `Account`, `Transaction`, `SavingsGoal`, and `Reward`.

### Configuration
- **Database**: Configured with H2 in-memory database (`jdbc:h2:mem:testdb`) for testing.
- **Query Logging**: Optional query logging enabled via `payforjoy.database.enable-query-logging` property.

### Sample Data Initialization
- Automatically initializes sample data (users, accounts, transactions, goals, and rewards) if no existing data is found.
- Includes:
    - A demo user with pre-configured accounts.
    - Sample transactions for income and expenses.
    - Savings goals for emergency funds and vacations.
    - Rewards for milestones.

### Technologies Used
- **Java**: Core programming language.
- **Spring Boot**: Framework for building the application.
- **Maven**: Dependency management and build tool.
- **SQL**: Database interactions.
- **JavaScript**: For frontend integration (if applicable).

## How to Access H2 Console
- URL: `http://localhost:8081/h2-console`
- Ensure the following properties are set in `application.properties`:
  ```properties
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  

