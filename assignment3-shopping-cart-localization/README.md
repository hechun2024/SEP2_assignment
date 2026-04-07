# Assignment 2 - Shopping Cart UI

JavaFX shopping cart app with multilingual UI support.

## Features

- JavaFX UI built with `FXML` (`src/main/resources/main.fxml`)
- Cart total calculation logic in `CartCalculator`
- Language switching via resource bundles (`MessagesBundle*.properties`)
- Unit tests for cart calculation (`CartCalculatorTest`)

## Tech Stack

- Java 17 (Maven compiler release)
- JavaFX 21.0.5
- Maven
- JUnit 5 + JaCoCo

## Quick Start

```bash
mvn clean test
mvn javafx:run
```

Build JAR:

```bash
mvn clean package
```

## Internationalization (i18n)

Language files are in `src/main/resources`:

- `MessagesBundle.properties` (default)
- `MessagesBundle_en_US.properties`
- `MessagesBundle_fi_FI.properties`
- `MessagesBundle_ja_JP.properties`
- `MessagesBundle_sv_SE.properties`
- `MessagesBundle_ar_AR.properties`

## CI/CD and Docker

- `Jenkinsfile` runs build, test, JaCoCo report, Docker build, and Docker push.
- `Dockerfile` packages the built JAR and runs it with JavaFX modules.

