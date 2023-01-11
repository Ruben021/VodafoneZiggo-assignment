# King Louie's Orders

### Postman collection
A postman collection is included in this repository. It can be found in the folder `postman`.  
This collection contains a couple of requests.  
- The `GET` request to retrieve all orders. It tests if the response is OK and if the response is an array.
- The `POST` request to create an order. There are 3 different request created for this to test if it works as required
  - Create an order, expect `OK`
  - Create an order as a user that is not found in the https://reqres.in/api/users, expect `NOT_FOUND` user does not exist.
  - Create an order but the user already ordered this before. The first request has to be made before this will work. Expect `CONFLICT` user already ordered product.

### OpenAPI files
The OpenAPI files can be found at this folder: `src/main/resources/openapi`. This folder contains 2 files:
- `orders.yaml` this is the file used to generate the API definition of this service
- `users.yaml` this is the file used to generate a java client for API at https://reqres.in/api/users

### Tests
To run all tests run `./mvnw clean test` in the root folder of this project.

### Run the application locally
To run the application from the commandline run the command 
```
./mvnw spring-boot:run
```

### Run the application in docker
First build the docker image with this command:
```
$ docker build -t king-louies-orders .
```  
Then run the container with this command: 
```
docker run -p 8080:8080 -t king-louies-orders
```

### Swagger ui
The swagger ui can be found at the url: http://localhost:8080/swagger-ui/index.html