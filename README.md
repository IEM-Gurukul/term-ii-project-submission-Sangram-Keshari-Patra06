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
- Dynamic Dashboard: Real-time calculation of total balance using Live Data.
- Category Management: Pre-defined and custom categories for efficient tracking.
- Persistence: Local storage using the Room Persistence Library (SQLite abstraction).
- Data Visualization: Integrated charts to show percentage-based spending per category.
- Search & History: Filterable transaction history by date range or amount.

---

## OOP Concepts Used

- Abstraction: Implementation of Data Access Objects (DAO) to define database interactions without exposing internal SQL logic.
- Inheritance: Use of extends for UI components (e.g., BaseActivity) and inheriting from AndroidViewModel to maintain state across configuration changes.
- Polymorphism: Method overriding in RecyclerView Adapters (e.g., onBindViewHolder) to handle different data display logic dynamically.
- Exception Handling: Implementation of try-catch-finally blocks for database I/O operations and handling NullPointerExceptions during view binding.
- Collections / Threads: Use of List<Transaction> and ArrayList for data sorting. Multithreading
  is handled via Executors or AsyncTask (legacy/simplified) to ensure database operations don't block the Main UI Thread.

---

## Proposed Architecture Description
The app follows the MVVM (Model-View-ViewModel) pattern, which is the industry standard for robust Android apps built in Java. The View (XML/Activity) is responsible for the UI; the ViewModel retrieves data from the repository and exposes it; the Model consists of the Room Database and Entities. This separation ensures that the business logic and the UI are separate, making the code easier to maintain, test, and allow room for improvements.

---

## Git Discipline Notes
Minimum 10 meaningful commits required.


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
    ├── MainFrame.java
    └── AppPanel.java (consolidated UI)
```
---