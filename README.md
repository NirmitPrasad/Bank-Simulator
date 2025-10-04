# Bank-Simulator
Bank Simulator is a full-featured backend project that simulates real-world banking operations using Spring Boot, JPA/Hibernate, and RESTful APIs. It provides secure, modular, and scalable backend services for managing accounts, transactions, and notifications — perfect for learning, testing, or integrating with a frontend banking dashboard.

🚀 Key Features

💳 Account Management

Create, update, and delete customer accounts

Fetch account details with real-time balance updates

Prevent duplicate account creation using validation rules

💸 Transaction System

Deposit, withdraw, and transfer money between accounts

Automatically record every transaction with timestamps

Handle transaction rollback on failures (ACID-compliant via @Transactional)

📧 Email Notifications

Send email alerts on successful deposits, withdrawals, and transfers

Configurable SMTP setup for real email delivery

Template-based notification messages

📊 Transaction History & Reporting

View all transactions by account or date range

Pagination and sorting support for large data sets

JSON-formatted API responses

🧩 Error Handling & Validation

Custom exceptions for invalid accounts, insufficient balance, and more

Centralized exception handling using @ControllerAdvice

Input validation with Hibernate Validator annotations

🧠 Additional Highlights

Fully documented REST APIs with Swagger / OpenAPI

Integration tests with SpringBootTest and MockMvc

DTO-based request/response layers for clean data flow

Layered architecture (Controller → Service → Repository → Entity)

🛠️ Tech Stack
Layer	              Technologies
Backend             Framework	Spring Boot 3+, Spring Data JPA
Database	          MySQL / H2 (for testing)
Testing	            JUnit 5, MockMvc, RestTemplate
Notifications	      JavaMailSender
Build Tool	        Maven / Gradle

📂 Project Structure
com.bfe.route.enums
 ├── controller/         → REST API endpoints  
 ├── services/           → Business logic (AccountService, TransactionService)  
 ├── entity/             → JPA entities (Account, Transaction)  
 ├── repository/         → Spring Data repositories  
 ├── dto/                → Data Transfer Objects  
 ├── exception/          → Custom exception handling  
 └── config/             → App configurations (email, db, etc.)

🌐 Example API Endpoints
Method	   Endpoint	                     Description
POST	     /api/account/create	         Create new bank account
GET	       /api/account/{id}	           Fetch account details
POST	     /api/transaction/deposit	     Deposit money
POST	     /api/transaction/withdraw	   Withdraw money
POST	     /api/transaction/transfer	   Transfer between accounts
GET	       /api/transaction/all	         View all transactions

💡 Future Enhancements

🔐 JWT-based authentication
💼 Role-based access (Admin, Customer)
📱 Integration with React or Angular frontend
🪙 Support for multi-currency transactions

🧾 License

This project is open-source and available under the MIT License.
