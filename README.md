# Java Broker App Challenge

This project implements a backend API for a brokerage firm, where employees can manage stock orders and handle customer funds. The application is built using Spring Boot and H2 database. 
The API includes endpoints for creating, listing, deleting orders, depositing and withdrawing money, and more.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Endpoints](#endpoints)
- [Setup](#setup)
- [Bonus Features](#bonus-features)

## Features
- **Create, List, Delete Orders:** Manage stock orders for customers.
- **Deposit and Withdraw Money:** Handle customer deposits and withdrawals.
- **Order Matching:** Update asset sizes when orders are matched.
- **Authorization:** Admin-based security to protect the endpoints.
- **Customer-Specific Data Access:** Customers can only access and manipulate their own data.


## Requirements
  - Spring Boot
  - H2 Database
  - Java 17
  - Maven
  - JUnit

## Endpoints
  - **Create Customer**

      POST /customer
      Create a customer with TRY Asset.
    
  - **Create Order**

      POST /order
      Create a new stock order for a customer.
  
  - **List Orders**

      GET /list
      List all orders for a customer with optional date filters.

  - **Delete Order**

      DELETE /order/{orderId}
      Delete a pending order.

  - **Deposit Money**

      POST /asset/{customerId}/deposit
      Deposit money for a given customer.

  - **Withdraw Money**

      POST /asset/{customerId}/withdraw
      Withdraw money for a customer using an IBAN.

  - **List Assets**

      GET /{customerId}/assets
      List all assets for a customer.

### Security
- **Admin Authorization:** All endpoints require admin credentials.
- **Customer Authorization:** Each customer can only manage their own data (Bonus feature).
  

## Setup

  ### Clone the repository
  ```bash
  git clone <repository-url>
  cd <repository-folder>
  ```
  
  ### Build the project
  ```bash
  mvn clean install
  ```
  
  ### Run the application
  ```bash
  mvn spring-boot:run
  ```
  
  ### Access the H2 Database
  The H2 database console can be accessed at:
  ```
  http://localhost:8080/h2-console
  ```
  Make sure the JDBC URL is:
  ```
  jdbc:h2:file:./data/mydatabase
  ```
## Bonus Features

1. **Customer Table and Authorization:**
   - Customers can only access and manipulate their own data. 
   - Admins can access and manipulate data for all customers.
   
2. **Order Matching:**
   - Admins can match pending orders via a special endpoint, which updates asset sizes for both TRY and the stock assets involved.

        
