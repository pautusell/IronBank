# IronBank PTA 

💰 💸 The banking API 💰 💸

#### Introduction

This project simulates a bank backend, that allows Users to Manager different types of Accounts and perform Transactions.

## SetUp

Download the code on your local machine and run it on your IDE. Then you can use it with the following PostMan Collection:

https://www.postman.com/pautupostman/workspace/ironbank-postman-ws/documentation/24840310-cd9dde1e-1a6e-43cf-ad37-087490b83bbc

The database connection is an external online mySQL database, so no need set up your own.

### Demo Data

Under the Profile DEMO, the project has an extensive DataLoader that uses random data to generate users, accounts and transactions of all kinds, so you can already start using all endpoints.


## General Information

### Accounts
The system has 4 types of accounts: 
1.	Checking
2.	StudentChecking
3.	Savings
4.	CreditCard

#### Checking Accounts
When creating a new Checking account, if the primaryOwner is less than 24, a StudentChecking account is created, otherwise a regular Checking Account.
Minimum Balance: 250
Monthly Maintenance Fee 12

#### Student Checking Accounts
No conditions or Fees


#### Savings Accounts
Default interest rate: 0.0025
Maximum interest rate: 0.5
Default minimum balance: 1000
Minimum balance may be instantiated with a value less than 1000 but no lower than 100

#### CreditCard Accounts
Default credit limit: 100 
Credit limit may be set higher than 100 but not higher than 100000
Default interest rate: 0.2
Interest rate may be set less than 0.2 but not lower than 0.1

### Users
The system has 3 types of Users: 
1.	Admins
2.	AccountHolders
3.	ThirdParty Accounts

#### AccountHolders
The username must be unique and of NIF format.
AccountHolders can only access their own accounts with the correct credentials using Basic Auth.

#### Admins
Can create new accounts (Checking, Savings, or CreditCard Accounts) and new Users (AccountHolders, Admin).
Admins can access the balance for any account and modify it, edit its conditions and properties, and force any allowed operation on accounts of any AccountHolder.

#### ThirdParty
In order to receive and send money, Third-Party Users must provide their hashed key in the header of the HTTP request. They also must provide the amount, the Account id and the account secret key.


## Interest and Fees
PenaltyFee for all accounts is 40.
If any account drops below the minimumBalance, the penaltyFee is automatically deducted from the balance.
Interest on savings accounts is added to the account annually at the rate of specified interestRate per year.
Interest on credit cards is added to the balance monthly.
Transactions
Any operation involving money, generates a Transaction.
Transactions are stored in a different repository, and are related to the corresponding account.
As an example, a transfer generates 2 Transactions: one in the origin account and another in the destination account (if is also internal from this same bank). 
Application of fees and interests also generates Transactions.

## Operations
Possible operations per account are represented in the following table.

![image](https://user-images.githubusercontent.com/115667649/216781906-dfb6c309-46e5-4b7d-9bb9-e634fca7e63a.png)


## Validations
The system runs several validations, before processing transactions.
1.	Check that the user performing the request is either the account Owner (either primary or secondary). Admins are always authorized per this validation. 
2.	Check that the balance or credit limit is enough, for Transactions that consume money.
3.	Fraud Validations: see next chapter.
4.	Check for account status (not frozen).
5.	Check for minimum balance penalty, if it needs to be applied, previous (just to recheck) or after the transaction.
6.	Check the secret key
7.	Check that the Third Party is registered with the Bank/User.

### Fraud Detection
The system have a fraud detection feature that recognizes patterns that indicate suspicious activity. 
When potential fraud is detected, it can freeze the account status or ask for user confirmation, to prevent further unauthorized transactions.
The system curently monitors transactions for these patterns:

-	Transactions made in a 24 hour period that total more than 150% of the customer's highest daily total transactions in any other 24 hour period.
-	An individual transaction amount that is higher than 150% of the average transaction amount of the last 3 month
-	More than 2 transactions occurring on a single account within a 1 second period.


## Scheduled Tasks
The system has several scheduled methods, in the form of daily night batches, to perform update operations.
All accounts are processed daily, but only updated if necessary according to the business defined parameters.
For efficiency and bandwidth, the retrieval of the BD is in 2 steps: first a list of all record ids, and then the objects are brough individually to be processed as needed.

Currently there are four scheduled methods:
1.	applyMaintenanceFee
2.	applySavingsInterest
3.	applyCreditInterest
4.	updateHistoricMaxAmounts


## Reports
- The user can generate an Account Statement for any of their accounts, with the details of the account and a list of the last 100 transactions.
- The system is able to generate reports of an account transaction history for any given selected period.
- The Admin can generate a report of all the Accounts in the bank, with their details.


## Security
Endpoints are secured with Basic Auth, currently with 2 roles: USER and ADMIN.
AccountHolders are ROLE_USER by default, and Admins are ROLE_USER,ROLE_ADMIN.
Some operations do not require a logged request:
1.	Create AccountHolder
2.	Password Reset
3.	Credit card purchases (since the user is not authenticated, but the card shares the secretKey
4.	Third Party operations, since they are validated on header hashedKey and the secretKey of the target account.


## Technical Specs
-	Coded in Java and Spring Boot.
-	MySQL database to store data, currently an online MySQL DB.
-	Spring Security for authentication.


## Future additions
The following functionalities are not yet implemented, but are jotted down as next steps.
*	Implement a feature that allows account holders to set up automatic transfers between accounts, such as a savings account and a checking account.
*	Implement a feature that allows account holders to set up recurring payments, such as paying bills automatically.
*	Implement a feature that allows account holders to set up notifications for specific account activities, such as low balance or suspicious activity.




## Annex: Conceptual Definitions

#### Account Holder
Account holder users are customers of the bank who have one or more accounts with the bank. They have access to the system to view their account information, perform transactions, and manage their account settings. Account holders have a direct relationship with the bank and are able to access the system using their personal information and credentials, such as a username and password.

#### Admin
Admin users are employees of the bank who have access to the system to manage and maintain the system. They have access to the system to view and modify account information, perform transactions, and manage system settings. 

#### Third Party User
Third party users are external entities that have access to the system to perform specific actions. These users are typically organizations or individuals that provide services to the bank or its customers. They do not have access to customer account information, but they are able to perform specific actions such as processing transactions or providing account information to customers.

#### Checking account
A checking account is a type of bank account that allows customers to deposit and withdraw funds, write checks, and make electronic transactions. Checking accounts typically have lower interest rates than savings accounts, but they also have lower fees and minimum balance requirements. These accounts are designed to be used for everyday transactions, such as paying bills and making purchases.

#### StudentChecking account
A student checking account is a type of checking account that is designed specifically for students. These accounts have features that are tailored to the needs of students, such as lower fees and minimum balance requirements.

#### Saving account
A savings account is a type of bank account that allows customers to deposit and withdraw funds, and earn interest on their deposits. Savings accounts typically have higher interest rates than checking accounts, but they also have higher fees and minimum balance requirements. These accounts are designed to be used for long-term savings and to grow the customer's wealth.

#### Credit Card
A credit card account is a type of account that allows customers to make purchases and withdraw cash on credit. Credit card accounts typically have a credit limit, which is the maximum amount that can be borrowed, and an interest rate, which is the rate at which interest is charged on the outstanding balance. The customer must pay back the borrowed amount plus interest. These accounts are designed to be used for short-term borrowing and to make purchases that the customer may not be able to afford upfront.
