# E-Store: CourseWork

An online E-store system built with Angular and Spring for selling online courses.

## Team

- Cole Johnson
- Shandon Mith
- Rhythm K C
- Andrew Chang
- Hunter Davenport

## Prerequisites

- Java 11
- Maven
- Node.js >=16 & NPM

## How to run it

1. Clone the repository and go to the `estore-api`.
2. Execute `mvn compile exec:java`
3. Open a new terminal and navigate to the `estore-api` directory.
4. Execute `npm install && npm start`
5. Open your browser to `http://localhost:4200/`

## Known bugs and disclaimers

- The course form does not validate or limit all inputs
- Course description text has no length limit in course card
- Text does not wrap correctly on course description page

## How to test it

The Maven build script provides hooks for run unit tests and generate code coverage
reports in HTML.

To run tests on all tiers together do this:

1. Execute `mvn clean test jacoco:report`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/index.html`

To run tests on a single tier do this:

1. Execute `mvn clean test-compile surefire:test@tier jacoco:report@tier` where `tier` is one of `controller`, `model`, `persistence`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/{controller, model, persistence}/index.html`

To run tests on all the tiers in isolation do this:

1. Execute `mvn exec:exec@tests-and-coverage`
2. To view the Controller tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
3. To view the Model tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
4. To view the Persistence tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`

\*(Consider using `mvn clean verify` to attest you have reached the target threshold for coverage)

## How to generate the Design documentation PDF

1. Access the `PROJECT_DOCS_HOME/` directory
2. Execute `mvn exec:exec@docs`
3. The generated PDF will be in `PROJECT_DOCS_HOME/` directory

## License

MIT License

See LICENSE for details.
