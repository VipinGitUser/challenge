# challenge
Below changes are done in existing project.
1. Added /transfer method and dto for transfering money.
2. Centralized exception handler with added exceptions.
3. Test cases for added features.
4. Added jacoco to generate test coverage report.
5. Added swagger for api documentation.
6. Configured actuator for application monitoring and metrics.
7. Configured to generate log file.

Due to time constraints, I could implement a subset of features. With more time, I can achieve the following additional features.

1. Decoupled transfer service to handle transfer request efficiently using message broker like kafka or rabbitMQ.
2. Transactional transfer service with database to enforce ACID behaviour. 
3. Configuration profiles for each environment.
4. Asymmetric encryption of any sensitive properties in profiles using JCE.
5. Integrate SonarQube for code quality. 
6. Implementing timeout , Retry and circuitbreaker using resilience4j for fault tolerance.
7. Integrating Spring Security for authenticated and authorized requests, utilizing OAuth or JWT for enhanced security.
8. logging using ELK stack.
9. Dockerized application with CI/CD pipeline.
10. sleuth and zipkin in case the application request tracing is required. 




