[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)
# PCCCS495 – Term II Project

## Project Title : Expense Tracker

---

## Problem Statement (max 150 words)
“Expense Tracker Pro” is an offline-first Java application that prevents "budget leakage" by providing secure, instant transaction logging without cloud dependency. Its robust MVVM architecture ensures data integrity during state changes, allowing users to visualize financial health efficiently on the go.

---

## Target User
- Students
- freelancers
- professionals 

---

## Core Features

- CRUD Operations: Create, Read, Update, and Delete transactions (Income/Expenses).
- Dynamic Dashboard: Real-time calculation of total balance with live data updates.
- Category Management: Pre-defined and custom categories for efficient tracking.
- Persistence: Local storage using SQLite database.
- Data Visualization: Integrated charts (Pie Chart, Bar Chart) to show spending distribution by category.
- Search & History: Filterable transaction history by date range or amount.
- Three-Tab Interface: Dashboard, Transactions, and Categories management in a single window.

---

## OOP Concepts Used

- **Abstraction**: Implementation of Data Access Objects (DAO) to define database interactions without exposing internal SQL logic.
- **Inheritance**: Use of extends for UI components and inheriting from base service classes to maintain consistent behavior.
- **Polymorphism**: Method overriding in different panel classes (DashboardPanel, TransactionsPanel, CategoriesPanel) to handle different data display logic dynamically.
- **Exception Handling**: Implementation of try-catch-finally blocks for database I/O operations and handling NullPointerExceptions during view binding.
- **Collections**: Use of List<Transaction>, ArrayList, and Map for data sorting and categorization. HashMaps store expense-by-category data for pie chart visualization.
- **Threading**: Database operations are handled to prevent blocking the Main UI Thread.

---

## Proposed Architecture Description
The app follows the MVVM (Model-View-ViewModel) pattern, which is the industry standard for robust applications built in Java. 
- **View Layer**: Three separate UI panels (DashboardPanel, TransactionsPanel, CategoriesPanel) managed by MainFrame with tabbed navigation.
- **ViewModel/Service Layer**: AppService provides business logic and retrieves data from the database without exposing implementation details to the UI.
- **Model Layer**: Entities (Transaction, Income, Expense, Category) and the SQLite Database.

This separation ensures that the business logic and the UI are separate, making the code easier to maintain, test, and allowing room for improvements.

---

## How to Run

### Prerequisites:
- Java JDK 8 or higher
- Maven 3.6+
- git

### Setup Instructions:
1. clone the repository
2. build the project using maven
   ```bash
   mvn clean package
   ```
4. run the application
   ```bash
   java -jar target/expense-tracker-1.0.0.jar
   ```
   or, if you use the shaded JAR (with dependencies):
   ```bash
   java -jar target/expense-tracker-1.0.0-jar-with-dependencies.jar
   ```

### Notes
- The application uses an SQLite database file, which will be created automatically in the project directory on first run.
- If you encounter any issues with missing dependencies, ensure Maven has internet access to download them.

---
# file structure of the application
```
com.expensetracker/
├── AppException.java (handles all errors)
├── AppService.java (all business logic)
├── AppUtils.java (all utilities)
├── DatabaseManager.java (all DB operations)
├── ExpenseTrackerApp.java (entry point)
├── models/
│   ├── Category.java
│   ├── Transaction.java
│   ├── Income.java
│   ├── Expense.java
│   └── TransactionFilter.java
└── ui/
    ├── MainFrame.java          (main application window with tabbed interface)
    ├── DashboardPanel.java     (dashboard with charts and statistics)
    ├── TransactionsPanel.java  (transaction management interface)
    └── CategoriesPanel.java    (category management interface)
```

## Git Discipline Notes
Minimum 10 meaningful commits required.

---
