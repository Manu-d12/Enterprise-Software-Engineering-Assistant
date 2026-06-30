# AI Software Engineering Assistant - 2 Month Project Roadmap

## Project Goal

Build an **AI Software Engineering Assistant** for Java/Spring Boot developers.

The application will allow users to:

- Register and login
- Create software projects
- Upload requirement documents or coding standards
- Ask questions from uploaded documents using RAG
- Generate Spring Boot code from requirements
- Generate a downloadable ZIP file containing Java project/module files
- Review Java code using AI
- Generate requirement analysis and architecture suggestions

This project is designed for a **3 YOE Java Developer** resume with strong focus on:

- Java
- Spring Boot
- Spring AI
- REST APIs
- Spring Security
- React JS
- Vite
- JavaScript
- GenAI
- RAG
- Vector Database
- Docker
- System Design

---

# Final Project Name

## AI Software Engineering Assistant

One-line description:

> An AI-powered developer assistant built using Java 21, Spring Boot 3, Spring AI, React JS, Vite, JavaScript, and RAG to automate requirement analysis, Spring Boot code generation, code review, and project knowledge search.

---

# High-Level Architecture

```text
React JS + Vite Frontend
      |
      v
Spring Boot Backend with Spring AI
      |
      v
LLM API + Vector Database
      |
      v
PostgreSQL / ChromaDB
```

---

# Tech Stack

## Frontend

- React JS
- Vite
- JavaScript
- Axios
- React Router
- Material UI or Tailwind CSS

## Java Backend

- Java 21
- Spring Boot 3
- Spring AI
- Spring Web
- Spring Security
- JWT Authentication
- Spring Data JPA
- PostgreSQL
- LLM integration through Spring AI ChatClient
- Embeddings and vector search through Spring AI
- Maven

## AI and RAG

- OpenAI / Gemini API
- ChromaDB
- Spring AI VectorStore integration
- Prompt templates
- Structured JSON responses

## DevOps

- Docker
- Docker Compose
- GitHub
- GitHub Actions optional
- AWS EC2 optional

---

# Core Features

## 1. Authentication

Users can:

- Register
- Login
- Access protected APIs using JWT

## 2. Project Workspace

Users can:

- Create a project
- View all projects
- Open project dashboard
- Store generated outputs against each project

## 3. Document Upload

Users can upload:

- PDF
- TXT
- DOCX later
- Coding standards
- Requirement documents

## 4. RAG Chatbot

Users can ask questions like:

```text
What are the business rules?
What API standards should I follow?
What is the login flow?
```

AI answers using uploaded project documents.

## 5. Requirement Analyzer

User enters:

```text
Build employee leave management system
```

AI generates:

- Functional requirements
- Non-functional requirements
- Actors
- Entities
- APIs
- Edge cases
- Business rules

## 6. Spring Boot Code Generator

User enters:

```text
Generate Employee CRUD module with name, email, department, salary.
```

AI generates files like:

```text
Employee.java
EmployeeDTO.java
EmployeeRepository.java
EmployeeService.java
EmployeeServiceImpl.java
EmployeeController.java
GlobalExceptionHandler.java
ApiResponse.java
```

## 7. ZIP Download

Instead of Monaco editor, the first version will generate a downloadable ZIP.

The ZIP can contain:

```text
employee-crud.zip

src/main/java/com/example/demo/
├── entity/Employee.java
├── dto/EmployeeDTO.java
├── repository/EmployeeRepository.java
├── service/EmployeeService.java
├── service/impl/EmployeeServiceImpl.java
├── controller/EmployeeController.java
├── exception/GlobalExceptionHandler.java
└── response/ApiResponse.java
```

Advanced version can generate a full Maven project:

```text
employee-management.zip

├── pom.xml
├── README.md
├── src/main/java/...
├── src/main/resources/application.yml
└── src/test/java/...
```

## 8. Code Review Assistant

User uploads Java code or pastes code.

AI reviews:

- Code smells
- SOLID violations
- Missing validations
- Security issues
- Performance issues
- Better Spring Boot practices

## 9. Output History

Store generated outputs:

- Requirement analysis
- Generated code metadata
- Code review reports
- RAG chat history

---

# 2 Month Timeline

Assumption:

- 1 hour per day
- AI tools will be used for writing code
- Focus is resume-ready MVP, not enterprise production product

Total duration:

```text
8 weeks
Approx. 56 hours
```

---

# Phase 1 - Project Setup and Architecture

## Duration

Week 1

## Goal

Set up both applications:

- React JS + Vite frontend
- Spring Boot backend with Spring AI

## Functionality to Build

### Spring Boot

Create backend project with:

- Java 21
- Spring Boot 3
- Maven
- Spring Web
- Spring Security
- Spring Data JPA
- Spring AI
- Spring AI OpenAI or Gemini starter
- PostgreSQL driver
- Lombok
- Validation

Initial packages:

```text
backend-java/src/main/java/com/aiassistant/backend
├── config
├── controller
├── dto
├── entity
├── repository
├── service
├── security
└── exception
```

### React JS + Vite

Create frontend app with:

- Login page
- Register page
- Dashboard placeholder
- Routing setup
- Axios setup

Recommended project path:

```text
frontend-react
├── src
├── package.json
├── vite.config.js
└── README.md
```

## APIs to Build

### Spring Boot

```http
GET /api/health
```

## Deliverables

At end of week 1:

- React JS + Vite app running
- Spring Boot app running
- GitHub repo created
- Basic README added

## Resume Value

Shows:

- Full-stack project setup
- Java backend ownership
- Spring AI architecture

---

# Phase 2 - Authentication and Project Workspace

## Duration

Week 2

## Goal

Build user authentication and project management.

## Functionality to Build

### Authentication

User should be able to:

- Register
- Login
- Receive JWT token
- Access secured APIs

### Project Workspace

User should be able to:

- Create project
- View project list
- Open project details page

## Database Tables

### users

```sql
id
name
email
password
role
created_at
```

### projects

```sql
id
user_id
name
description
created_at
updated_at
```

## Spring Boot APIs

```http
POST /api/auth/register
POST /api/auth/login
GET /api/projects
POST /api/projects
GET /api/projects/{id}
DELETE /api/projects/{id}
```

## React Screens

- Register page
- Login page
- Dashboard
- Create Project modal/page
- Project details page

## Deliverables

At end of week 2:

- JWT authentication working
- User can login
- User can create project
- User can view project dashboard

## Resume Value

Shows:

- Spring Security
- JWT
- REST API design
- PostgreSQL
- React JS + Vite integration

---

# Phase 3 - Spring AI Integration

## Duration

Week 3

## Goal

Make Spring Boot communicate directly with the LLM through Spring AI.

## Functionality to Build

Create a basic GenAI flow:

```text
React JS + Vite -> Spring Boot -> Spring AI -> LLM -> Spring Boot -> React
```

## Feature

Simple AI prompt screen:

User enters:

```text
Explain what a REST API is.
```

System returns AI response.

## Spring Boot APIs

```http
POST /api/ai/prompt
```

Request:

```json
{
  "prompt": "Explain REST API"
}
```

Response:

```json
{
  "response": "A REST API is..."
}
```

## Important Implementation

Spring Boot should use Spring AI:

Recommended:

```text
Use ChatClient for clean LLM calls from the service layer.
```

## Deliverables

At end of week 3:

- React JS + Vite can send prompt
- Spring Boot receives prompt
- Spring AI calls the LLM
- AI response displayed in React

## Resume Value

Shows:

- Java backend as main API layer
- Spring AI integration
- LLM orchestration inside Spring Boot
- GenAI API integration

---

# Phase 4 - Document Upload and RAG Foundation

## Duration

Week 4

## Goal

Allow users to upload documents and store embeddings for RAG.

## Functionality to Build

User can upload project documents:

- TXT first
- PDF if time allows
- DOCX optional

System will:

1. Store uploaded file metadata in PostgreSQL
2. Extract text in Spring Boot
3. Split text into chunks
4. Generate embeddings using Spring AI
5. Store embeddings in ChromaDB through Spring AI VectorStore

## Database Table

### documents

```sql
id
project_id
file_name
file_type
file_path
status
created_at
```

## Spring Boot APIs

```http
POST /api/projects/{projectId}/documents/upload
GET /api/projects/{projectId}/documents
```

## RAG Processing

Spring Boot should:

- Read text
- Split into chunks
- Generate embeddings with Spring AI
- Store in ChromaDB with metadata:

```json
{
  "projectId": 1,
  "fileName": "coding-standards.txt",
  "chunkIndex": 1
}
```

## React Screens

In project details page:

- Upload document button
- Document list
- Upload status

## Deliverables

At end of week 4:

- User can upload document
- Document text processed by Spring Boot
- Embeddings stored in ChromaDB
- Uploaded files listed in project

## Resume Value

Shows:

- RAG foundation
- File upload
- Vector database
- Document processing
- Enterprise AI use case

---

# Phase 5 - RAG Chatbot and Requirement Analyzer

## Duration

Week 5

## Goal

Build useful AI features from uploaded project documents.

## Feature 1: Project Knowledge Chatbot

User asks:

```text
What coding standard should controller classes follow?
```

System should:

1. Search ChromaDB for relevant chunks using projectId
2. Add chunks to LLM prompt
3. Generate answer using Spring AI ChatClient
4. Return answer with source file names

## Spring Boot API

```http
POST /api/projects/{projectId}/chat
```

Request:

```json
{
  "question": "What API response format should I use?"
}
```

Response:

```json
{
  "answer": "You should use ApiResponse<T> for all APIs.",
  "sources": ["coding-standards.txt"]
}
```

## Feature 2: Requirement Analyzer

User enters:

```text
Build employee leave management system.
```

AI returns:

- Summary
- Functional requirements
- Non-functional requirements
- Actors
- Entities
- REST APIs
- Edge cases
- Acceptance criteria

## Spring Boot API

```http
POST /api/projects/{projectId}/requirements/analyze
```

## Store Output

Create table:

### ai_outputs

```sql
id
project_id
type
title
content
created_at
```

Output types:

```text
RAG_CHAT
REQUIREMENT_ANALYSIS
CODE_GENERATION
CODE_REVIEW
```

## React Screens

- Chat tab
- Requirement Analyzer tab
- Output history section

## Deliverables

At end of week 5:

- RAG chatbot working
- Requirement analyzer working
- Sources shown in answer
- AI outputs saved in database

## Resume Value

Shows:

- Real RAG implementation
- Context-aware GenAI
- Prompt engineering
- AI output persistence

---

# Phase 6 - Spring Boot Code Generator with ZIP Download

## Duration

Week 6

## Goal

Build the most important resume feature: AI code generator.

## Main Flow

```text
User enters module requirement
        |
React JS + Vite sends request to Spring Boot
        |
Spring Boot builds a structured prompt
        |
Spring AI sends prompt to the LLM
        |
LLM returns JSON file list
        |
Spring Boot creates ZIP
        |
User downloads ZIP
```

## User Input Example

```text
Generate Employee CRUD module with fields:
name, email, department, salary.
Use Spring Boot 3, Java 21, JPA, DTO, validation, service layer, repository, and global exception handling.
```

## Generated Files

Minimum files:

```text
Employee.java
EmployeeDTO.java
EmployeeRepository.java
EmployeeService.java
EmployeeServiceImpl.java
EmployeeController.java
GlobalExceptionHandler.java
ApiResponse.java
```

Better version:

```text
pom.xml
application.yml
README.md
src/main/java/com/example/employee/entity/Employee.java
src/main/java/com/example/employee/dto/EmployeeDTO.java
src/main/java/com/example/employee/repository/EmployeeRepository.java
src/main/java/com/example/employee/service/EmployeeService.java
src/main/java/com/example/employee/service/impl/EmployeeServiceImpl.java
src/main/java/com/example/employee/controller/EmployeeController.java
src/main/java/com/example/employee/exception/GlobalExceptionHandler.java
src/main/java/com/example/employee/response/ApiResponse.java
```

## Spring Boot API

```http
POST /api/projects/{projectId}/code/generate
```

Request:

```json
{
  "moduleName": "Employee",
  "packageName": "com.example.employee",
  "requirement": "Generate Employee CRUD module with name, email, department, salary",
  "includeTests": false,
  "includeReadme": true
}
```

Response:

```text
employee-crud.zip
```

## Spring AI Response Shape

Spring AI should ask the LLM to return this JSON shape:

```json
{
  "moduleName": "Employee",
  "files": [
    {
      "path": "src/main/java/com/example/employee/entity/Employee.java",
      "content": "package com.example.employee.entity; ..."
    },
    {
      "path": "src/main/java/com/example/employee/controller/EmployeeController.java",
      "content": "package com.example.employee.controller; ..."
    }
  ]
}
```

## ZIP Generation in Spring Boot

Spring Boot should:

- Receive file list from Spring AI response
- Create ZIP using ZipOutputStream
- Add each generated file as ZipEntry
- Return file as downloadable response

## Important Prompt Rules

Spring AI prompt should force the LLM to return valid JSON only.

Prompt should include:

```text
You are a senior Java Spring Boot developer.
Generate production-ready Spring Boot 3 code.
Use Java 21.
Use Jakarta Validation.
Use constructor injection.
Use layered architecture.
Do not put business logic in controller.
Return only valid JSON with files array.
Each file must have path and content.
```

## RAG Enhancement

If project has uploaded coding standards, include them in prompt:

```text
Use the following project coding standards:
{retrieved_context}
```

This makes code generation project-specific.

## React Screen

Code Generator tab:

Fields:

- Module name
- Package name
- Requirement
- Include README checkbox
- Include tests checkbox later
- Generate ZIP button

## Deliverables

At end of week 6:

- User can generate Spring Boot CRUD module
- User can download ZIP
- ZIP contains multiple Java files
- Generated code follows layered architecture
- Output metadata stored in database

## Resume Value

This is the strongest feature.

Shows:

- Java/Spring Boot expertise
- GenAI code generation
- Prompt engineering
- File generation
- ZIP download
- Enterprise developer productivity tool

---

# Phase 7 - Code Review Assistant and Unit Test Generator

## Duration

Week 7

## Goal

Add features that make the project look complete and practical.

## Feature 1: Code Review Assistant

User can paste Java code or upload a Java file.

AI returns:

- Summary
- Issues
- Severity
- Suggested improvements
- Refactored code optional

## Spring Boot API

```http
POST /api/projects/{projectId}/code/review
```

Request:

```json
{
  "fileName": "EmployeeService.java",
  "code": "public class EmployeeService { ... }"
}
```

Response:

```json
{
  "summary": "The code is clean but missing validation.",
  "issues": [
    {
      "severity": "MEDIUM",
      "category": "Validation",
      "message": "Email format is not validated."
    }
  ]
}
```

## Review Categories

AI should check:

- SOLID principles
- Exception handling
- Validation
- Security
- Performance
- Readability
- Spring Boot best practices

## Feature 2: Unit Test Generator

User enters code or module requirement.

AI generates:

- JUnit 5 tests
- Mockito tests
- MockMvc tests

## Spring Boot API

```http
POST /api/projects/{projectId}/tests/generate
```

## Generated Test Files

```text
EmployeeServiceTest.java
EmployeeControllerTest.java
```

## React Screens

Add tabs:

- Code Review
- Test Generator

## Deliverables

At end of week 7:

- Code review assistant working
- Test generator working
- Results saved in history
- UI has separate tabs for major features

## Resume Value

Shows:

- AI-assisted software quality
- JUnit and Mockito awareness
- Code quality mindset
- Java interview relevance

---

# Phase 8 - Polish, Docker, README, Resume Preparation

## Duration

Week 8

## Goal

Make the project presentable for resume, GitHub, and interviews.

## Functionality to Finish

### UI Polish

Improve:

- Dashboard
- Project details page
- Forms
- Error handling
- Loading indicators
- Toast notifications

### Backend Polish

Add:

- Global exception handling
- API response wrapper
- Request validation
- Logs
- Clean DTOs
- Proper package structure

### Spring AI Polish

Add:

- Prompt templates
- Error handling
- Environment variable support
- Clean response parsing
- Fallback error messages

### Docker

Create:

```text
docker-compose.yml
```

Services:

```text
frontend-react
backend-java
postgres
chromadb
```

### README

Add strong GitHub README:

- Project overview
- Architecture diagram
- Tech stack
- Features
- Screenshots
- API list
- How to run locally
- Environment variables
- Future enhancements
- Resume bullets

### Screenshots

Take screenshots of:

- Login
- Dashboard
- Project creation
- Document upload
- RAG chat
- Requirement analyzer
- Code generator
- ZIP download
- Code review

## Deliverables

At end of week 8:

- Complete resume-ready MVP
- GitHub repo polished
- README completed
- Docker Compose added
- Screenshots added
- Resume bullets prepared

## Resume Value

Shows:

- End-to-end ownership
- Deployment readiness
- Clean documentation
- Full-stack GenAI system

---

# Daily Plan

## Daily Time

```text
1 hour per day
```

## Recommended Daily Structure

```text
10 minutes - revise previous work
40 minutes - implement one small task
10 minutes - test and commit
```

## Weekly Pattern

```text
Monday    - Backend task
Tuesday   - Backend task
Wednesday - Spring AI task
Thursday  - React JS + Vite integration
Friday    - Testing/debugging
Saturday  - Feature completion
Sunday    - README, cleanup, commit
```

---

# Minimum Viable Version

If time becomes tight, build only these features:

```text
1. Login/Register
2. Create Project
3. Spring AI integration
4. Requirement Analyzer
5. Code Generator
6. ZIP Download
7. Basic React JS + Vite UI
8. README
```

This is enough for resume.

---

# Strong Resume Version

To make it stronger, include:

```text
1. RAG chatbot
2. Document upload
3. ChromaDB
4. Code review assistant
5. Unit test generator
6. Docker Compose
```

---

# Advanced Future Enhancements

After 2 months, you can add:

- Monaco editor
- GitHub repository import
- Jira story generator
- Spring AI advisors and tool calling
- Architecture diagram generator
- PR review automation
- SonarQube integration
- AWS deployment
- GitHub Actions CI/CD

---

# Suggested GitHub Repository Structure

```text
ai-software-engineering-assistant

├── frontend-react
│   ├── src
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
│
├── backend-java
│   ├── src/main/java
│   ├── src/main/resources
│   ├── pom.xml
│   └── README.md
│
├── docker-compose.yml
├── README.md
└── docs
    ├── architecture.md
    ├── api-spec.md
    └── screenshots
```

---

# Suggested Database Tables

## users

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP
);
```

## projects

```sql
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    name VARCHAR(150),
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## documents

```sql
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT,
    file_name VARCHAR(255),
    file_type VARCHAR(50),
    file_path TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP
);
```

## ai_outputs

```sql
CREATE TABLE ai_outputs (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT,
    type VARCHAR(100),
    title VARCHAR(255),
    content TEXT,
    created_at TIMESTAMP
);
```

## generated_files

```sql
CREATE TABLE generated_files (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT,
    module_name VARCHAR(150),
    file_name VARCHAR(255),
    file_path TEXT,
    file_content TEXT,
    created_at TIMESTAMP
);
```

---

# Important APIs Summary

## Auth APIs

```http
POST /api/auth/register
POST /api/auth/login
```

## Project APIs

```http
GET /api/projects
POST /api/projects
GET /api/projects/{id}
DELETE /api/projects/{id}
```

## Document APIs

```http
POST /api/projects/{projectId}/documents/upload
GET /api/projects/{projectId}/documents
```

## RAG Chat APIs

```http
POST /api/projects/{projectId}/chat
```

## Requirement Analyzer APIs

```http
POST /api/projects/{projectId}/requirements/analyze
```

## Code Generator APIs

```http
POST /api/projects/{projectId}/code/generate
```

## Code Review APIs

```http
POST /api/projects/{projectId}/code/review
```

## Test Generator APIs

```http
POST /api/projects/{projectId}/tests/generate
```

---

# Prompt Templates

## Requirement Analyzer Prompt

```text
You are a senior business analyst and Java solution architect.

Analyze the following requirement and produce:
1. Summary
2. Functional requirements
3. Non-functional requirements
4. Actors
5. Business rules
6. Database entities
7. REST APIs
8. Edge cases
9. Acceptance criteria

Requirement:
{requirement}
```

## Code Generator Prompt

```text
You are a senior Java Spring Boot developer.

Generate production-quality Spring Boot 3 code.

Requirement:
{requirement}

Package name:
{packageName}

Module name:
{moduleName}

Project coding standards:
{ragContext}

Rules:
- Use Java 21
- Use Spring Boot 3
- Use Spring Data JPA
- Use Jakarta Validation
- Use DTO classes
- Use constructor injection
- Use layered architecture
- Keep business logic in service layer
- Use GlobalExceptionHandler
- Use ApiResponse<T>
- Return only valid JSON
- JSON must contain files array
- Each file must contain path and content
```

## Code Review Prompt

```text
You are a senior Java code reviewer.

Review the following Java/Spring Boot code.

Check:
1. SOLID principles
2. Clean code
3. Validation
4. Exception handling
5. Security
6. Performance
7. Spring Boot best practices

Return:
- Summary
- Issues with severity
- Suggested improvements
- Refactored code if needed

Code:
{code}
```

## Unit Test Generator Prompt

```text
You are a senior Java test engineer.

Generate JUnit 5 and Mockito tests for the following Java code.

Rules:
- Use JUnit 5
- Use Mockito
- Use AssertJ if needed
- Cover success and failure scenarios
- Return test files as JSON with path and content

Code:
{code}
```

---

# Interview Explanation

Use this explanation:

> I built an AI Software Engineering Assistant where Spring Boot acts as the main backend and uses Spring AI for LLM calls, RAG, embeddings, and prompt templates. The React JS + Vite frontend handles the user workspace, while Spring Boot manages authentication, PostgreSQL persistence, document processing, code generation, ZIP downloads, code review, and unit test generation.

---

# Resume Bullets

Use these bullets:

```text
Built an AI Software Engineering Assistant using Java 21, Spring Boot 3, Spring AI, React JS, Vite, JavaScript, PostgreSQL, and ChromaDB to automate requirement analysis, code generation, code review, and project knowledge retrieval.

Designed a full-stack architecture where Spring Boot acts as the primary API layer and uses Spring AI ChatClient, embeddings, and vector search for LLM orchestration and RAG workflows.

Implemented a GenAI-powered Spring Boot code generator that converts feature requirements into downloadable Java project ZIP files containing entities, DTOs, repositories, services, controllers, exception handlers, and API response classes.

Developed a RAG-based project knowledge chatbot using document chunking, embeddings, and ChromaDB to answer questions from uploaded project documents with source references.

Implemented secure authentication and project workspace management using Spring Security, JWT, PostgreSQL, and role-based API access.
```

---

# Final 2 Month Outcome

By the end of 2 months, you should have:

```text
1. GitHub repository
2. Working React JS + Vite frontend
3. Working Spring Boot backend with Spring AI
4. PostgreSQL database
5. ChromaDB vector database
6. Requirement analyzer
7. RAG chatbot
8. Code generator with ZIP download
9. Code review assistant
10. Good README
11. Screenshots
12. Resume bullets
```

This is enough to confidently present yourself as a:

```text
Java Spring Boot Developer with GenAI project experience
```
