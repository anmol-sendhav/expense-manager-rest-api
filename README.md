# ExpensePilot – Full Stack Expense Management Application

ExpensePilot is a full-stack expense management application built using **Java Spring Boot, React, and MySQL**. It helps users manage their personal finances by tracking income, expenses, budgets, and financial reports through a modern and responsive interface.

The application includes secure JWT-based authentication, dashboard analytics, notifications, PDF reports, and an AI-powered chatbot to assist users with financial queries.

## Features

### Authentication & Security
- User Registration and Login
- JWT Authentication and Authorization
- Password Encryption using BCrypt
- Protected REST APIs
- User-specific data access

### Expense Management
- Add, View, Update, and Delete Expenses
- Category-wise Expense Tracking
- Expense Filtering
- Secure User-specific Expense Records

### Income Management
- Add, View, Update, and Delete Income
- Track Multiple Income Sources
- Income History

### Budget Management
- Create and Manage Budgets
- Track Budget Utilization
- Monitor Spending

### Dashboard & Analytics
- Total Income and Expenses
- Current Balance
- Savings Rate
- Income and Expense Statistics
- Financial Overview

### AI Financial Assistant
- AI-powered chatbot using Spring AI
- Integration with Groq API
- Ask financial and budgeting-related questions
- Conversational responses through the application

### Notifications
- View Notifications
- Unread Notification Count
- Mark Notifications as Read
- Mark All Notifications as Read
- Delete Notifications

### Reports & Profile
- Generate Financial Reports
- PDF Export
- Manage User Profile
- Profile Photo Management

## Technology Stack

### Backend
- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT Authentication
- Hibernate
- Spring AI
- Maven
- Lombok
- ModelMapper / MapStruct

### Frontend
- React
- JavaScript
- HTML5
- CSS3
- Axios
- React Router

### Database
- MySQL

### AI Integration
- Spring AI
- Groq API
- GPT-OSS-20B Model

### Tools & Deployment
- Git and GitHub
- Postman
- Docker
- Docker Compose

## Project Architecture

```text
ExpensePilot
│
├── Backend
│   ├── src/main/java/com/expense
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── exception
│   │   ├── mapper
│   │   ├── repository
│   │   ├── security
│   │   └── service
│   │
│   └── src/main/resources
│
├── expensepilot-frontend
│   ├── src
│   │   ├── components
│   │   ├── context
│   │   ├── pages
│   │   └── api
│   └── package.json
│
├── Dockerfile
├── compose.yaml
├── .dockerignore
├── .env.example
└── README.md
```

## API Endpoints

### Authentication

| Method | Endpoint |
|---|---|
| POST | `/auth/register` |
| POST | `/auth/login` |

### Expenses

| Method | Endpoint |
|---|---|
| POST | `/expenses` |
| GET | `/expenses` |
| GET | `/expenses/{id}` |
| PUT | `/expenses/{id}` |
| DELETE | `/expenses/{id}` |

### Income

| Method | Endpoint |
|---|---|
| POST | `/income` |
| GET | `/income` |
| PUT | `/income/{id}` |
| DELETE | `/income/{id}` |

### Budgets

| Method | Endpoint |
|---|---|
| POST | `/budgets` |
| GET | `/budgets` |
| PUT | `/budgets/{id}` |
| DELETE | `/budgets/{id}` |

### AI Chatbot

| Method | Endpoint |
|---|---|
| POST | `/chat` |

### Notifications

| Method | Endpoint |
|---|---|
| GET | `/notifications` |
| GET | `/notifications/unread-count` |
| PUT | `/notifications/{id}/read` |
| PUT | `/notifications/read-all` |
| DELETE | `/notifications/{id}` |

*Other endpoints include dashboard, profile, profile photo, and reports.*

## Environment Configuration

Configure your environment variables using a `.env` file in the backend project root.

Example:

```env
GROQ_API_KEY=your_groq_api_key
```

Configure your MySQL database in `application.properties` using environment variables or your local database credentials.

**Important:** Never commit your actual `.env` file or API keys to GitHub. Use `.env.example` with placeholder values.

## Running the Backend

Clone the repository:

```bash
git clone https://github.com/anmol-sendhav/expense-manager-rest-api.git

cd expense-manager-rest-api
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

## Running the Frontend

Open another terminal:

```bash
cd expensepilot-frontend

npm install

npm run dev
```

Open the local URL provided by Vite in your browser.

## Docker

The project includes Docker configuration for containerizing the application.

```bash
docker compose up --build
```

Configure the required database and environment variables before starting the containers.

## Future Improvements

- AI chatbot with personalized financial insights using actual user financial data
- Real-time notifications
- Advanced financial analytics
- Cloud deployment
- Automated testing

## Author

**Anmol Sendhav**

BCA Final Year Student  
Aspiring Java Backend Developer

GitHub: https://github.com/anmol-sendhav
