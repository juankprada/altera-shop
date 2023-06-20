# Altera Icecream Shop

## Summary

This is a microservices oriented solution built in Java 17. The project is structured as follows:

### Inventory Microservice
**Directory:** `altera-shop/altera-shop-inventory`
This micro service contains the backend for the inventory system of the Icecream shop. I will handle the requests to get, create, edit and delete items from the inventory. Additionally it contains the endpoint to retrieve the sales based on a specified date range.

### Store Microservice
**Directory:** `altera-shop/altera-shop-store`
This micro service works as as a very very simple Shop Backend-for-Frontend service and provides an endpoint to accept Order requests (purchases).

The Store Microservice uses two methods of communication witht the Inventory Microservice.
One is a REST comunication when retrieveing inventory state in order to accept a purchase. The other is through Event Messages (Kafka) when placing an order.

The decission to make it this way was to decouple the most expensive operation of actually creating processing the Order and updating the Inventory when a purchase is made, allowing the Store microservice to handle more requests while keeping the lock of the request thread the least amount of time.

## Dependencies

The project was built using the following:

- Java 17 (OpenJDK for HotSpot and GraalVM for native builds)
- Quarkus 3
- Apache Kafka *required*
- Jeagger (For monitoring purposes)
- Graylog (For Logging)
- PostgreSQL (main database) *required*
- Docker (to handle the environment) *required*

### Considerations regarding the Dependencies
The idea was to build a Kubernetes native solution using GraalVM to produce native builds of the microservices. For this Quarkus provides the best toolset to build REST APIS while providing the means to generate native builds of the Java application using GraalVM.


## Build & Run


1. Go to the root of the project `altera-shop`
	- run `docker compose -f docker-compose/docker-compose-all-dependencies.yml up`

	This will start all dependency services needed:
		- PostgresDB
		- Greylog
		- Kafka
		- Jeagger

2. Go to `altera-shop/altera-shop-inventory` directory
	- execute `./mvnw quarkus:dev` to start the Inventory microservice in Dev mode

3. Go to `altera-shop/altera-shop-store` directory
	- execute `./mvnw quarkus:dev` to start the Inventory microservice in Dev mode

At this point the services are up and running and the application can be tested.

### Access Graylog and verify captured logs

> http://127.0.0.1:9000
> Username: admin
> Password: admin

Once logged in Greylog, configure the input stream

> System/Input --> Inputs
Select **GELF UDP** as Input and  then click on **Launch New Input** Button.

On the modal window, check the Global checkbox and set a Title for the *Input* as desired.

Click on **Launch Input**



## How to Test

### Add Product to Inventory
```
curl --location 'http://localhost:8081/api/v1/inventory' \
--header 'Content-Type: application/json' \
--data '{
	"sku": "SKU-001",
	"name": "Chocolate",
	"description": "The recommended one",
	"category": "Icecream",
	"price": "50",
	"inventory": 20
}'
```

### Get All Inventory data
```
curl --location 'http://127.0.0.1:8081/api/v1/inventory'
```


### Get Inventory by SKU
```
curl --location 'http://localhost:8081/api/v1/inventory/SKU-001'
```


### Get Inventory by Category
```
curl --location 'http://localhost:8081/api/v1/inventory/category/ICECREAM'
```


### Update Inventory entry
```
curl --location --request PUT 'http://localhost:8081/api/v1/inventory/SKU-001' \
--header 'Content-Type: application/json' \
--data '{
	"sku": "SKU-001",
	"name": "Chocolate",
	"description": "The recommended one",
	"category": "Icecream",
	"price": "90",
	"inventory": 20
}'
```

### Place an Order

```
curl --location 'http://localhost:8080/api/v1/orders' \
--header 'Content-Type: application/json' \
--data '{
	"items" : [
        {
            "sku": "SKU-001",
            "quantity": 1
        }
    ]
}'
```


### Verify Sales
```
curl --location 'http://localhost:8081/api/v1/sales
```


## OpenAPI documentation

Both Microservices have Swagger UI enabled with OpenAPI documentation.
To access it go to:

**Inventory API** [http://localhost:8081/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui](http://localhost:8081/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui)
**Orders API** [http://localhost:8080/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui](http://localhost:8080/q/dev-ui/io.quarkus.quarkus-smallrye-openapi/swagger-ui)


## Pending Work

Both microservices can be built as native images that run on both Docker Compose and Kubernetes. Currently the build process to produce those native images is set up and is possible to try them out. by following Quarkus documentation.

Neverthe less due to the time constrains I was not able to produce a working Docker Compose setup.

By following the instructions bellow it is possible to build and run the microservices as Docker Images, and eventhough both microservices will run, the KAFKA integration is not working as expected, causing the `Inventoroy`  quantity to never decrease with each purchase.

This is a known bug which is not really specific to the implementation of the microservices but to the configuration of the Docker Compose file.

If you wish to try it out bellow are the instructions to build the native images and run the containers.

### Running with Docker Compose

1. Go to `altera-shop/altera-shop-inventory` directory

	- execute `./mvnw clean package -Pnative -Dquarkus.native.container-build=true` to create Native Image

	- build docker image: `docker build -f src/main/docker/Dockerfile.native-micro -t quarkus/altera-shop-inventory .`


2. Go to `altera-shop/altera-shop-store` directory

	- execute `./mvnw clean package -Pnative -Dquarkus.native.container-build=true` to create Native Image

	- build docker image: `docker build -f src/main/docker/Dockerfile.native-micro -t quarkus/altera-shop-store .`

3. Go to the root of the project `altera-shop`
	- run `docker compose -f docker-compose/docker-compose-all-services.yml up`
	This command shuld run all containers necessary for the Altera Ice Cream Shop to run correctly.





