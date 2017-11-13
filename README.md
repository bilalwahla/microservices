[![Build Status](https://travis-ci.org/bilalwahla/microservices.svg?branch=master)](https://travis-ci.org/bilalwahla/microservices)

# Microservices

This project depicts understanding of microservice architecture, detailing few of the patterns that are essential to the success of a microservices project.

It is well understood that although microservices bring gifts of well-known architectural quality attributes of modularity, granularity, reusability, extensibility, testability, and maintainability to a system, it is far from an easy architecture to implement for all the challenges and complexities it introduces. Using this project, I will try to point out a few I have learned and technologies I used to implement them over the last couple of Java/Groovy & Spring framework based microservices projects.

## Configuration Management

Each microservice, being loosely coupled, independently implementable, testable, deployable, and maintainable, has its own unique configurations. Integration with databases or third party services, as well as other internal microservices, are a few of those unique configurations. Configuration changes without version control and code review means mutable servers (each representing a microservice) with manual, time-consuming configuration change traceability. Only segregated, centralized, distributed, and highly available configuration management can help organize configurations of a system consisting of several microservices, e.g. Spring Cloud Config, a resource-based API for external configuration (name-value pairs, or equivalent YAML content). Implementing a Configuration Server using Spring Cloud Config is just a matter of adding Spring Cloud Config dependencies, @EnableConfigServer annotation on the main class, and a properties file pointing to a version controlled repository like GitHub. Any microservice can simply add Spring Cloud Config client dependencies to their build and start using this server.

## Routing Patterns

### Service Discovery

When you have a few microservices with each running multiple instances, where each of those instances is being brought up and down all the time as the microservice is being scaled based on demand, a service client should not need to know the IP and port of a service. The solution is to use a service discovery that is highly available, load balanced, resilient, and fault tolerant. Leaving it to the IT operations team to do it at the infrastructure level means additional work for the business and its maintenance, as well as moving away from DevOps and continuous delivery, a couple of extremely important microservices values.

Just like a configuration server, a discovery server is simple to setup using Spring Cloud Netflix (Eureka) dependencies and the @EnableEurekaServer annotation on the main class of the discovery server application. A client of this server can start using this server simply by adding Spring Cloud Netflix (Eureka) dependencies and the @EnableEurekaClient annotation on its main class. When an instance of a service discovery client service spins up, it registers its IP address with the service discovery and starts sending a heartbeat to it. This means that service discovery knows when any of its clients have died, subsequently removing the IP of the dead instance. If the discovery server is scaled up, all nodes share health information about all instances of each client service.

### Services Gateway

Separating concerns and implementing them as microservices still leaves us with a few things that have to happen in each of the microservices. Primary among these cross-cutting concerns are static and dynamic routing, authentication and authorization [using Spring Cloud Security/OAuth2/JWT], metric collection, auditing and logging [using Spring Cloud Sleuth]. These should be implemented in one place without any duplicated (and possibly corrupted) efforts across multiple microservices (by multiple teams). A services gateway using Spring Cloud Netflix (Zuul) is one solution that can sit between your microservices application and the outside world and receive any inbound requests for any of the microservices:

 * It maps the incoming calls to the downstream services using automated mapping of routes via service discovery.
 * Services Gateway is also where you want to write custom logic that will be applied to all the service calls flowing through the gateway. Spring Cloud Netflix (Zuul) allows you to do just that using filters within the gateway. Two main filters are pre-filter that is primarily used for authentication and authorization using Spring Cloud Security/OAuth2/JWT and post-filter is what is used for logging using Spring Cloud Sleuth.

Setting up a Zuul server is again, as you would expect from Spring, as simple as adding dependencies to the class path and an @EnableZuulProxy annotation on the main class, and your services gateway service is ready to go. Of course, there are configurations that you need to apply should you wish for additional functionality and/or customize how you wish certain things to behave, e.g. you could make it look up services using the discovery service either by their default service IDs, or you can define mapping against those service IDs, maybe to make URLs to your services gateway simpler and/or smaller.

Basic useful feature list:

 * Ctrl+S / Cmd+S to save the file
 * Ctrl+Shift+S / Cmd+Shift+S to choose to save as Markdown or HTML
 * Drag and drop a file into here to load it
 * File contents are saved in the URL so you can share files

## Client Resiliency Patterns

Various microservices talking to each other through well-implemented routing patterns is well and good, but often have I seen a service client falling apart due to a fault not of its own, but of the service itself, e.g. the service gradually degraded, started performing badly, and made the service client perform badly, subsequently bringing the entire application down. Client resiliency should act as a protection layer between the service client and the service. There are various patterns that should be implemented to make the service clients resilient, a few of which are mentioned below:

### Circuit Breaker Pattern

This should be implemented to monitor a call, and if the call takes too long, it should be intercepted and killed. All calls to the service should be monitored, making sure that if enough calls fail, the circuit breaker pops.

### Fallback Pattern

This should be implemented if possible for either an alternative resource or queueing for future processing.

### Bulkheading

Bulkheading is grouping various remote calls each into their own thread pool, preventing one slow service call from impacting others.

Each microservice in the project (i.e. licensing-service and organisation-service) is making a remote call either to a database or another service and for that reason implements these client resiliency patterns using Spring Cloud Netflix (Hystrix) to prevent the entire system from falling apart in case of any problems. With Spring Cloud Netflix (Hystrix) dependencies in the classpath, @EnableCircuitBreaker annotation on the main class, and @HystrixCommand annotation on the method that makes the remote call, is all there is to get going with the above patterns (please see the API documentation for details).
