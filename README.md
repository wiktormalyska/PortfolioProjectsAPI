# 📁 PortfolioProjectsAPI

**PortfolioProjectsAPI** is a backend application built with **Spring Boot** that manages project data displayed on a personal portfolio website. Projects are stored in a **MariaDB** database, and metadata can be easily imported from JSON files.

## ✨ Features

- REST API for adding, deleting, and retrieving portfolio projects
- Support for importing project metadata from a `meta.json` file
- Secure integration with GitHub using a Personal Access Token
- Environment-based configuration for database access

---

## 📦 Requirements

- Java 17+
- Spring Boot
- MariaDB (or a compatible database)
- Maven or Gradle
- (Optional) Docker & Docker Compose


---

## ⚙️ Configuration

Create a `.env` file in the project root directory based on the following example:

```dotenv
DATABASE_DRIVER_CLASS_NAME=org.mariadb.jdbc.Driver
DATABASE_URL=jdbc:mariadb://database.server.example
DATABASE_USERNAME=api_username
DATABASE_PASSWORD=api_password
META_FILE_NAME=meta.json
GITHUB_PERSONAL_ACCESS_TOKEN=ghp_abc123
```

### Example `meta.json`

```json
{
  "name": "Project ABC",
  "description": "Project Description",
  "imageUrl": "https://example.com/image.jpg",
  "technologies": [
    "Java",
    "Spring Boot",
    "MariaDB"
  ],
   "websiteUrl": "https://example.com"
}
```

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/wiktormalyska/PortfolioProjectsAPI.git
   cd PortfolioProjectsAPI
   ```

2. Configure the `.env` file and provide a valid `meta.json`.

3. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

4. The application will be available at `http://localhost:8080`

## 🐳 Run with Docker Compose
1. Make sure you have Docker and Docker Compose installed.
2. Create your .env and meta.json files as described above.
3. Run the app using:

```bash
docker-compose up --build
```
The Spring Boot app will be available at http://localhost:8080 and the MariaDB server will be available on port 3306.

---

## 🔌 API Endpoints (Example)

| Method | Endpoint         | Description                  |
|--------|------------------|------------------------------|
| GET    | `/projects`      | Retrieve all portfolio items |
| POST   | `/projects`      | Add a new project            |
| DELETE | `/projects/{id}` | Delete a project by ID       |

---

## 📝 License

This project is licensed under the MIT License. See the `LICENSE` file for more details.
