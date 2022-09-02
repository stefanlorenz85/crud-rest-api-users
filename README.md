# RESTful CRUD Users API
This Spring Boot service provides serveral http endpoints for handling users data per JSON. Creating a new user also stores an encrypted default password. Service is tested with Java OpenJDK 11.


## Requirements
* Optional: Install Git https://git-scm.com/book/en/v2/Getting-Started-Installing-Git
* Check out the repository on GitHub via Git or download from https://github.com/stefanlorenz85/crud-rest-api-users
* Install OpenJava JDK (Only test on OpenJDK 11) https://adoptium.net/temurin/releases/?version=11
* Maven https://maven.apache.org/download.cgi

## Commands

### Start service
The service starts an H2 in-memory database. Some intital data is loaded. The application.properties is not representing any production configuration and can be seen as a dev mode. The default port is `8080`. Spring Security is `disabled`.

To start the service:
```sh
mvn spring-boot:run
```
----
### Health Status
Actuator is used to access health metrics.
http://localhost:8080/actuator/health

----
### Run tests
To run tests:
```sh
mvn clean test
```

----
### Code coverage
The code coverage can be calculated with:
```sh
mvn jacoco:report
```
To see report open `./target/site/jacoco/index.html`

----
## Consoles & API Documentation
### H2 Console
Username and password are stored in `application.properties`. This data need to be a secreet for production or testing enviroments, only use locally.

See http://localhost:8080/h2-console

----

### Swagger
To test the API use the Swagger http://localhost:8080/swagger-ui/index.html]. Also Actuator endpoints are listed.


The endpoints are not secured and can be used without any authentification and authorization.

`NOTE`: SpringDoc is used https://springdoc.org/.
Springfox Swagger currently not working with Spring 2.7 https://github.com/springfox/springfox/issues/3462.


