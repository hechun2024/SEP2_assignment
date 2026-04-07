# Assignment 3 - Shopping Cart with Database & Localization

JavaFX shopping cart with multilingual support (5 languages) and MariaDB persistence. All shopping records saved with timestamp.

## Quick Start

### 1. Database Setup

```bash
# Initialize schema
mysql -u root -p < schema.sql

# Or with Docker
docker run --name mariadb-shopping \
  -e MARIADB_ROOT_PASSWORD=root \
  -e MARIADB_USER=shopuser \
  -e MARIADB_PASSWORD=123456 \
  -p 3306:3306 -d mariadb:latest

sleep 5
docker exec -i mariadb-shopping mysql -u shopuser -p123456 shopping_cart_localization < schema.sql
```

### 2. Build & Run

```bash
cd assignment3-shopping-cart-localization

# Compile + Test
mvn clean test

# Run Application
mvn javafx:run
```

Or from JAR:
```bash
mvn clean package
java --module-path $JAVAFX_SDK/lib --add-modules javafx.controls,javafx.fxml \
  -jar target/assignment3-shopping-cart-localization-1.0-SNAPSHOT.jar
```

### 3. Using the App

1. Select language (English, Finnish, Swedish, Japanese, **Arabic RTL**)
2. Click "Confirm Language"
3. Enter item count → Fill prices/quantities → "Calculate Total"
4. **Records auto-saved to database** ✓

## Features

- **Multilingual UI**: English (LTR) + Finnish + Swedish + Japanese + Arabic (RTL auto-mirror)
- **Database Backend**: Cart records + items stored in MariaDB with timestamp
- **Localization from DB**: Strings loaded from `localization_strings` table (fallback: properties files)
- **Unit Tests**: 6 passing tests (CartCalculator)
- **Docker Support**: Containerized with JavaFX 21
- **CI/CD**: Automated Jenkins pipeline

## Project Structure

```
src/main/java/com/hechun/shoppingcart/
├── App.java                    # JavaFX entry point
├── Controller.java             # UI + language switching + RTL logic
├── CartCalculator.java         # Calculation logic
├── CartService.java            # Save cart records to DB
├── LocalizationService.java    # Fetch strings from DB
├── DatabaseConnection.java     # MariaDB connection
├── CartItem.java               # Model
└── LanguageSelector.java       # Legacy console selector

src/main/resources/
├── main.fxml                   # JavaFX layout
└── MessagesBundle*.properties  # Fallback bundles (5 languages)

schema.sql                       # Database schema + seed data
pom.xml                          # Maven config
Dockerfile                       # Container image
Jenkinsfile                      # CI/CD pipeline
```

## Database Tables

| Table | Purpose |
|-------|---------|
| `cart_records` | Shopping sessions: total_items, total_cost, language, **timestamp** |
| `cart_items` | Items per session: price, quantity, subtotal (FK to cart_records) |
| `localization_strings` | Translated UI text: language, key, value (75 rows × 5 languages) |

## Tech Stack

- Java 17 + JavaFX 21.0.5
- MariaDB 3.3.2 JDBC
- Maven + JUnit 5 + JaCoCo
- Docker + Jenkins

## RTL Support (Arabic)

When "Arabic" selected, the entire UI:
- Mirrors node orientation (right-to-left)
- Aligns text to right
- Reverses container layout flow

Implementation: `Controller.applyTextDirection()` (lines 104–137)

## Troubleshooting

### Database Connection Error
```bash
# Verify DB is running
mysql -u shopuser -p123456 -h localhost shopping_cart_localization -e "SELECT COUNT(*) FROM localization_strings;"
# Should return: 75
```

### JavaFX Not Found
```bash
# Download: https://gluonhq.com/products/javafx/
export JAVAFX_SDK=/path/to/javafx-sdk-21
mvn javafx:run
```

### Localization Not Showing
```bash
# Check DB data
mysql -u shopuser -p123456 shopping_cart_localization -e \
  "SELECT language, COUNT(*) FROM localization_strings GROUP BY language;"
# Should show: ar_AR (15), en_US (15), fi_FI (15), ja_JP (15), sv_SE (15)
```

## Verify Submission Checklist

✅ Source code: `src/main/java/` (8 classes)  
✅ Database schema: `schema.sql` (3 tables + 75 seed rows)  
✅ Dockerfile: Builds JAR with JavaFX 21  
✅ Jenkinsfile: CI/CD pipeline (build → test → Docker → push)  
✅ README: This file + setup instructions  
✅ Tests: 6 passing (CartCalculatorTest)  

## Screenshots for Submission

1. **Database (Terminal)**:
   ```bash
   mysql -u shopuser -p123456 shopping_cart_localization -e \
     "SELECT * FROM cart_records; SELECT * FROM cart_items;"
   ```

2. **Application**:
   - English UI (LTR): Language selector + calculated total
   - Arabic UI (RTL): Same, but mirrored layout

## Build & Deploy

### Maven
```bash
mvn clean package
java -jar target/assignment3-shopping-cart-localization-1.0-SNAPSHOT.jar
```

### Docker
```bash
docker build -t shopping-cart-app .
docker run -e MARIADB_HOST=host.docker.internal -it shopping-cart-app
```

### Jenkins
All stages configured in `Jenkinsfile` to:
1. Build (mvn clean install)
2. Test (mvn test)
3. Code Coverage (jacoco:report)
4. Docker build & push to Docker Hub

## Author

He Chun | Software Engineering Project 2

---

**Questions?** Check database connection with `DatabaseConnection.main()` or review `schema.sql` for table structure.
