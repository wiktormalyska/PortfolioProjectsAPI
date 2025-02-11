# PortfolioProjectsAPI

### .env File Example
```dotenv
DATABASE_DRIVER_CLASS_NAME=org.mariadb.jdbc.Driver
DATABASE_URL=jdbc:mariadb://database.server.example
DATABASE_USERNAME=api_username
DATABASE_PASSWORD=api_password
META_FILE_NAME=meta.json
```

### Example meta.json file
```json
{
  "name": "Project ABC",
  "description": "Project Description",
  "imageUrl": "https://example.com/image.jpg",
  "technologies": [
    "Java",
    "Spring Boot",
    "MariaDB"
  ]
}
```