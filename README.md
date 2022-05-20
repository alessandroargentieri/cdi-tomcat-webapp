# Weld integration into Tomcat

The purpose of this project is to demonstrate the CDI integration in a Java web application.

The CDI library that has been chosen is [Weld](https://weld.cdi-spec.org/), already integrated with [TomEE](https://tomee.apache.org/) Application Server.

In this case, we're going to integrate this library in a Java web application deployed in a [tomcat](https://tomcat.apache.org/) servlet container.

## Run application

This application has been provided with a Dockerfile to avoid deploying the generated war file into a locally installed tomcat.
To run this application, first build the container image:

```bash
$ docker build -t myaccount/cdi .
```
After the image is ready run it on your machine:

```bash
$ docker run -it -p 8080:8080 --name mycdi cdi:latest
```
Then you can [cURL](https://curl.se/) the servlet:

```bash
$ curl http://localhost:8080/cdi/greetz
$ curl http://localhost:8080/cdi/greetz?name=Mario&surname=Rossi
$ curl http://localhost:8080/cdi/greetz?name=Mario&surname=Rossi&shaded=true
```

## How to integrate Weld

Here is a brief explanation of how to integrate Weld into your project.

### Maven import

Add Weld dependency in your project `pom.xml` file:

```xml
<dependency>
   <groupId>org.jboss.weld.servlet</groupId>
   <artifactId>weld-servlet-shaded</artifactId>
   <version>3.1.6.Final</version>
</dependency>
```

### Add Weld BeanManager into the `context.xml` file

After having added the maven dependency, register the _Weld BeanManager_ in your `src/main/webapp/META-INF/context.xml` file as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="BeanManager"
              auth="Container"
              type="javax.enterprise.inject.spi.BeanManager"
              factory="org.jboss.weld.resources.ManagerObjectFactory" />
</Context>
```

### Define an empty `beans.xml` file

even if you decide to declare your beans through the annotations, you must provide your project with a `src/main/webapp/WEB-INF/beans.xml` file:

```xml
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_2_0.xsd"
       bean-discovery-mode="all" version="2.0">
</beans>
```

### Define your beans

When you have to define a class from which Weld has to generate a bean, it's recommended to use the `@ManagedBean` annotation.

```java
import javax.annotation.ManagedBean;

@ManagedBean
class Car implements Vehicle { 
    ...
}
```

To inject the Weld generated bean (which is `@ApplicationScoped` by default) you use the annotation `@Inject`:

```java
import javax.inject.Inject;

class VehicleRegistry {
    
    @Inject
    private Vehicle vehicle;
    
    ...
}
```

If more than a single implementation of the same interface exists, you can specialize the injection using custom annotations:

```java
class VehicleRegistry {

    @Inject @Car
    private Vehicle car;

    @Inject @Bike
    private Vehicle bike;
    
    ...
}
```

where the custom annotations are defined as follows:

```java
@Qualifier
@Target({TYPE, METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
@interface Car {}
```

```java
@Qualifier
@Target({TYPE, METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
@interface Bike {}
```

And the respective beans are declared by using these two _qualifiers_:

```java
import javax.annotation.ManagedBean;

@ManagedBean
@Car
class Car implements Vehicle {
    ...
}
```

```java
import javax.annotation.ManagedBean;

@ManagedBean
@Bike
class Bike implements Vehicle {
    ...
}
```