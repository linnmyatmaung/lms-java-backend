# LMS Java Backend 🚀

![Java](https://img.shields.io/badge/Java-21-007396?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?logo=docker&logoColor=white)

A **Learning Management System (LMS)** backend written in **Java**. This application is designed for managing roles, users, and authentication in a robust, secure, and scalable manner.

---

## Project Structure 📂

### **Main Features**
- **Role Management**: Implementation for managing roles in the system.
- **User Management**: REST APIs for handling user CRUD operations.
- **Authentication & Security**:
    - Role-based security powered by **JWT (JSON Web Token)**.
    - Customizable configurations provided through `SecurityConfig.java`.

### **File Structure**
```plaintext
src/main/java/com/lucus/lms_java_backend/
├── api/
│   ├── role/
│   │   └── model/Role.java         # Role entity definition
│   ├── user/
│       ├── controller/UserController.java    # User API endpoints
│       └── service/impl/UserServiceImpl.java # User-related business logic
├── security/
│   ├── config/SecurityConfig.java   # Security configuration
│   ├── controller/AuthController.java   # Authentication APIs
│   ├── service/
│   │   ├── AuthService.java         # Auth service interface
│   │   └── impl/AuthServiceImpl.java # Auth service implementation
│   └── utils/JwtUtil.java           # JWT utilities
```

---

## Getting Started 🛠️

### **Prerequisites**
- **Java** 21
- **Maven** 3.x
- **Docker** (optional, for containerization)
- Any IDE (e.g., IntelliJ IDEA)

### **Setup Instructions**
1. Clone the repo:
   ```bash
   git clone https://github.com/your-repo/lms-java-backend.git
   cd lms-java-backend
   ```

2. Build the project with Maven:
   ```bash
   ./mvnw clean install
   ```

3. Configure environment variables in **`application.properties`**:
    - Specify database credentials.
    - Configure JWT secret, expiration time, etc.

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Access the APIs at:
   ```
   http://localhost:8080
   ```

---

## Deployment with Docker 🐳
1. Build the Docker image:
   ```bash
   docker build -t lms-java-backend .
   ```

2. Run the Docker container:
   ```bash
   docker run -p 8080:8080 lms-java-backend
   ```

---

## APIs Overview 📖

### Authentication APIs
- **Login**: `POST /auth/login`
- **Register**: `POST /auth/register`
- **Refresh Token**: `POST /auth/token/refresh`

### User Management
- **Get All Users**: `GET /users`
- **Get User by ID**: `GET /users/{id}`
- **Create User**: `POST /users`
- **Delete User**: `DELETE /users/{id}`

---

## Tech Stack 🧰

- **Backend**: Java 21, Spring Boot
- **Build Tool**: Maven
- **Security**: Spring Security, JWT
- **Database**: (Configure your database in `application.properties`)
- **Containerization**: Docker

---

## Contributing 🤝

Contributions are always welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Create a **Pull Request**.

---

## License 📄
This project is open-source and licensed under the **MIT License**.

---

## Feedback ✨

If you have any feedback or issues, feel free to open a GitHub issue or send a message to [your-email@example.com](mailto:your-email@example.com).

---

**Happy Coding! 🎉**