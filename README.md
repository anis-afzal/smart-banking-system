# 🏦 Smart Banking Management System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge\&logo=java\&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge\&logo=mysql\&logoColor=white)

> A robust, full-stack console banking application built from scratch using Core Java and MySQL. This project demonstrates strong Object-Oriented Programming (OOP) principles, Relational Database Management, and secure credential handling.

---

## 🌟 Key Features

### 🔐 Secure Authentication

* Users can create banking accounts with a secure 4-digit PIN.
* Authentication system protects user data and banking operations.
* Login validation ensures only authorized users can access accounts.

### 💰 Core Banking Operations

* Account creation.
* Balance inquiry.
* Cash deposits.
* Cash withdrawals.
* Transaction history tracking.

### 🔄 ACID-Compliant Fund Transfers

* Secure money transfer between accounts.
* Uses SQL transaction management.
* Implements `commit()` and `rollback()` to maintain data integrity.
* Prevents partial transactions during failures.

### 🛡️ Enterprise-Level Security

* Database credentials are never hardcoded.
* Uses Environment Variables (`System.getenv()`).
* Protects sensitive configuration from source code exposure.
* Suitable for secure deployment practices.

### 👨‍💼 Hidden Administrator Portal

* Password-protected admin dashboard.
* Monitor banking records.
* Manage customer accounts.
* Track unauthorized login attempts.
* Security auditing and activity monitoring.

---

## 🏗️ System Architecture

The application follows a layered console-based architecture:

```text
User Interface (Console)
          │
          ▼
Business Logic Layer
          │
          ▼
JDBC Database Layer
          │
          ▼
MySQL Database
```

### Components

* Main Application Controller
* Authentication Module
* Banking Operations Module
* Fund Transfer Module
* Transaction Manager
* Admin Dashboard
* Database Connection Manager

---

## 💻 Tech Stack

| Component             | Technology                        |
| --------------------- | --------------------------------- |
| Programming Language  | Java (JDK 8+)                     |
| Database              | MySQL                             |
| Database Connectivity | JDBC                              |
| Driver                | mysql-connector-j                 |
| IDE                   | IntelliJ IDEA / Eclipse / VS Code |
| Architecture          | Monolithic Console Application    |

---

## ⚙️ Prerequisites

Before running the application, install:

### 1. Java Development Kit

* JDK 8 or higher

### 2. MySQL Server

* MySQL Community Server
* Default Port: `3306`

### 3. IDE

Choose one:

* IntelliJ IDEA
* Eclipse
* VS Code

### 4. JDBC Driver

* mysql-connector-j

---

# 🛠️ Installation Guide

## Step 1: Clone Repository

```bash
git clone https://github.com/anis-afzal/smart-banking-system.git
cd smart-banking-system
```

---

## Step 2: Create Database

Run the following SQL script:

```sql
CREATE DATABASE smart_bank;

USE smart_bank;

CREATE TABLE accounts (
    account_number INT PRIMARY KEY AUTO_INCREMENT,
    account_holder VARCHAR(100) NOT NULL,
    pin VARCHAR(4) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00
);

CREATE TABLE transactions (
    tx_id INT PRIMARY KEY AUTO_INCREMENT,
    account_number INT,
    tx_type VARCHAR(20),
    amount DECIMAL(15,2),
    tx_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number)
    REFERENCES accounts(account_number)
);

ALTER TABLE accounts AUTO_INCREMENT = 10001;
```

---

## Step 3: Configure JDBC Driver

### IntelliJ IDEA

1. Open Project Structure.
2. Select Libraries.
3. Add `mysql-connector-j.jar`.
4. Apply changes.

### Eclipse

1. Right Click Project.
2. Build Path.
3. Add External JARs.
4. Select mysql-connector-j.jar.

---

## Step 4: Configure Environment Variables

For security reasons, database credentials are loaded from environment variables.

### Windows

```cmd
set DB_USER=root
set DB_PASSWORD=your_password
```

### Linux / macOS

```bash
export DB_USER=root
export DB_PASSWORD=your_password
```

### IntelliJ Run Configuration

Add:

```text
DB_USER=root
DB_PASSWORD=your_password
```

---

## Step 5: Run Application

Compile and execute:

```bash
javac Main.java
java Main
```

Or run directly from your IDE.

---

# 📂 Project Structure

```text
smart-banking-system/
│
├── src/
│   ├── Main.java
│   ├── DatabaseConnection.java
│   ├── AccountManager.java
│   ├── TransactionManager.java
│   ├── AdminPanel.java
│   └── Authentication.java
│
├── lib/
│   └── mysql-connector-j.jar
│
├── README.md
│
└── .gitignore
```

---

# 🏦 Sample Workflow

### User Registration

```text
Enter Name:
John Doe

Create PIN:
1234

Account Created Successfully
Account Number: 10001
```

### Login

```text
Enter Account Number:
10001

Enter PIN:
1234
```

### Deposit

```text
Deposit Amount:
5000

Deposit Successful
```

### Withdrawal

```text
Withdrawal Amount:
1000

Withdrawal Successful
```

### Fund Transfer

```text
Sender Account:
10001

Receiver Account:
10002

Amount:
500

Transfer Successful
```

---

# 🔒 Security Features

* Environment Variable Based Credentials
* Hidden Administrator Access
* Login Validation
* Unauthorized Access Logging
* Transaction Rollback Protection
* Database Integrity Enforcement
* Secure JDBC Connectivity

---

# 🚀 Future Enhancements

* GUI Version using JavaFX
* Web-Based Banking Portal
* OTP Verification
* Email Notifications
* Mobile Banking Application
* PDF Statements
* Role-Based Access Control
* Account Locking Mechanism
* Audit Logs
* REST API Integration

---

# 📊 Learning Outcomes

This project demonstrates:

* Object-Oriented Programming (OOP)
* Encapsulation
* Inheritance
* Polymorphism
* Abstraction
* JDBC Connectivity
* MySQL Database Design
* SQL Transactions
* Environment Variables
* Secure Coding Practices
* Exception Handling

---

# 🤝 Contributing

Contributions are welcome.

Steps:

1. Fork the repository.
2. Create a feature branch.

```bash
git checkout -b feature-name
```

3. Commit your changes.

```bash
git commit -m "Added new feature"
```

4. Push to GitHub.

```bash
git push origin feature-name
```

5. Open a Pull Request.

---

# 📜 License

This project is licensed under the MIT License.

```text
MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge,
to any person obtaining a copy of this software
and associated documentation files...
```

---

# 👨‍💻 Author

**Anis Afzal**

Developed as a learning-focused banking management system project using Java and MySQL.

⭐ If you found this project useful, consider giving it a star on GitHub.
