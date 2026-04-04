# Bank-Simulator
Bank Simulator is a full-featured backend project that simulates real-world banking operations using Spring Boot, JPA/Hibernate, and RESTful APIs. It provides secure, modular, and scalable backend services for managing accounts, transactions, and notifications — perfect for learning, testing, or integrating with a frontend banking dashboard.




## 🚀 Key Features




## 💳 Account Management


Create, update, and delete customer accounts

Fetch account details with real-time balance updates

Prevent duplicate account creation using validation rules



## 💸 Transaction System


Deposit, withdraw, and transfer money between accounts

Automatically record every transaction with timestamps

Handle transaction rollback on failures (ACID-compliant via @Transactional)



## 📧 Email Notifications


Send email alerts on successful deposits, withdrawals, and transfers

Configurable SMTP setup for real email delivery

Template-based notification messages



## 📊 Transaction History & Reporting


View all transactions by account or date range

Pagination and sorting support for large data sets

JSON-formatted API responses



## 🧩 Error Handling & Validation


Custom exceptions for invalid accounts, insufficient balance, and more

Centralized exception handling using @ControllerAdvice

Input validation with Hibernate Validator annotations




## 🧠 Additional Highlights


Fully documented REST APIs with Swagger / OpenAPI

Integration tests with SpringBootTest and MockMvc

DTO-based request/response layers for clean data flow

Layered architecture (Controller → Service → Repository → Entity)




## 🛠️ Tech Stack


Layer	              Technologies
Backend             Framework	Spring Boot 3+, Spring Data JPA
Database	          MySQL / H2 (for testing)
Testing	            JUnit 5, MockMvc, RestTemplate
Notifications	      JavaMailSender
Build Tool	        Maven / Gradle




## 📸 Screenshots

**🔑 Signup/Login Page**

<img width="1280" height="553" alt="image" src="https://github.com/user-attachments/assets/a71da591-5126-4d3c-8276-5169702e2bd3" />




**Personal Details**

<img width="1280" height="541" alt="image" src="https://github.com/user-attachments/assets/55350209-130a-4acc-a522-6254b499d649" />




**📧Email Verification**

<img width="826" height="810" alt="image" src="https://github.com/user-attachments/assets/5207bd7c-40bf-4c84-acde-80c3536553a3" />




**🔐Password Setup**

<img width="783" height="813" alt="image" src="https://github.com/user-attachments/assets/37f9d051-e6d1-40fb-8b4e-6d109f756fe4" />




**✅Account Creation**

<img width="544" height="630" alt="image" src="https://github.com/user-attachments/assets/43b9c987-9b47-4cd7-96eb-48964961e6b4" />




**🏠Home Page**

<img width="1280" height="540" alt="image" src="https://github.com/user-attachments/assets/9bd76b31-71cb-4396-a9b8-52582305dc59" />




**💵Transaction History**

<img width="1280" height="559" alt="image" src="https://github.com/user-attachments/assets/099ae573-7c62-43de-adfc-c6cce76c3c99" />




**📧Debit & Credit Email Notification**

![WhatsApp Image 2026-01-17 at 6 26 27 PM](https://github.com/user-attachments/assets/ea993c00-926f-4521-89a4-53e5116e0a4b)

![WhatsApp Image 2026-01-17 at 6 26 27 PM (1)](https://github.com/user-attachments/assets/13b91c20-8201-4026-ab24-2f8d0bdd541d)





## 📂 Project Structure


com.bfe.route.enums
 ├── controller/         → REST API endpoints  
 ├── services/           → Business logic (AccountService, TransactionService)  
 ├── entity/             → JPA entities (Account, Transaction)  
 ├── repository/         → Spring Data repositories  
 ├── dto/                → Data Transfer Objects  
 ├── exception/          → Custom exception handling  
 └── config/             → App configurations (email, db, etc.)




## 🌐 Example API Endpoints


Method	   Endpoint	                     Description
POST	     /api/account/create	         Create new bank account
GET	       /api/account/{id}	           Fetch account details
POST	     /api/transaction/deposit	     Deposit money
POST	     /api/transaction/withdraw	   Withdraw money
POST	     /api/transaction/transfer	   Transfer between accounts
GET	       /api/transaction/all	         View all transactions




## 💡 Future Enhancements


🔐 JWT-based authentication

💼 Role-based access (Admin, Customer)

📱 Integration with React or Angular frontend

🪙 Support for multi-currency transactions


## 🧾 License


This project is open-source and available under the MIT License.
