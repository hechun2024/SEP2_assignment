# Assignment 3 - Shopping Cart with Database & Localization

JavaFX shopping cart with 5 languages and MariaDB persistence.

## Requirements

- Java 17
- Maven
- Docker


## Startup Steps

This project depends on MariaDB running in Docker.  
If language switching does not work, it is usually because the database is not running or the schema was not initialized.

### 1. Stop local MariaDB if it is using port 3306
```bash
brew services stop mariadb
lsof -i :3306
```
### 2. Start Docker Desktop

Make sure Docker Desktop is running before continuing.

### 3. Start the MariaDB container

If the container does not exist yet:
```bash
docker run --name mariadb-shopping \
  -e MARIADB_ROOT_PASSWORD=root \
  -e MARIADB_DATABASE=shopping_cart_localization \
  -e MARIADB_USER=shopuser \
  -e MARIADB_PASSWORD=123456 \
  -p 3306:3306 \
  -d mariadb:latest
  ```
If the container already exists:
```bash
docker start mariadb-shopping
```
### 4. Initialize the database schema

Run this in the assignment3-shopping-cart-localization directory:
```bash
docker exec -i mariadb-shopping mariadb -u shopuser -p123456 shopping_cart_localization < schema.sql
```
### 5. Set environment variables
```bash
   export MARIADB_USER=shopuser
   export MARIADB_PASSWORD=123456
   export MARIADB_URL=jdbc:mariadb://localhost:3306/shopping_cart_localization
```
### 6. Run the project
```bash
   mvn javafx:run
```


### Run SonarQube analysis with java 21
```bash
./sonar.sh console
```
Then open http://localhost:9000 and run the analysis for this project.

### run sonarscanner in the project directory
```bash
sonar-scanner
```