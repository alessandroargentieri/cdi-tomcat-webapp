# Weld integration into Tomcat

```txt
 ▄     ▄ ▄▄▄▄▄▄▄ ▄▄▄     ▄▄▄▄▄▄    ▄   ▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄   ▄▄ ▄▄▄▄▄▄▄ ▄▄▄▄▄▄ ▄▄▄▄▄▄▄ 
█ █ ▄ █ █       █   █   █      █ ▄█ █▄█       █       █  █▄█  █       █      █       █
█ ██ ██ █    ▄▄▄█   █   █  ▄    █▄   ▄█▄     ▄█   ▄   █       █       █  ▄   █▄     ▄█
█       █   █▄▄▄█   █   █ █ █   █ █▄█   █   █ █  █ █  █       █     ▄▄█ █▄█  █ █   █  
█       █    ▄▄▄█   █▄▄▄█ █▄█   █       █   █ █  █▄█  █       █    █  █      █ █   █  
█   ▄   █   █▄▄▄█       █       █       █   █ █       █ ██▄██ █    █▄▄█  ▄   █ █   █  
█▄▄█ █▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄▄█▄▄▄▄▄▄█        █▄▄▄█ █▄▄▄▄▄▄▄█▄█   █▄█▄▄▄▄▄▄▄█▄█ █▄▄█ █▄▄▄█  
```

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
              type="jakarta.enterprise.inject.spi.BeanManager"
              factory="org.jboss.weld.resources.ManagerObjectFactory" />
</Context>
```

### Define an empty `beans.xml` file

even if you decide to declare your beans through the annotations, you must provide your project with a `src/main/resources/META-INF/beans.xml` file:

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
import jakarta.annotation.ManagedBean;

@ManagedBean
class Car implements Vehicle { 
    ...
}
```

To inject the Weld generated bean (which is `@ApplicationScoped` by default) you use the annotation `@Inject`:

```java
import jakarta.inject.Inject;

class VehicleRegistry {

    @Inject
    private Vehicle vehicle;
    
    ...
}
```

If more than a single implementation of the same interface exists, you can specialize the injection using the `@Named` annotations or custom qualifiers annotations as explained below.

#### Disambiguation using the `@Named` annotation

In order to let Weld understand what implementation of an interface to inject in a specific case you can use the `@Named` annotation on the `@ManagedBean` and during the injection phase:

```java
import jakarta.inject.Inject;
import jakarta.inject.Named;

class VehicleRegistry {

    @Inject @Named("car")
    private Vehicle car;

    @Inject @Named("bike")
    private Vehicle bike;
    
    ...
}
```

where the managed beans are labeled with the same attribute, to let the disambiguation happen:

```java
import jakarta.annotation.ManagedBean;
import jakarta.inject.Named;

@ManagedBean
@Named("car")
class Car implements Vehicle {
    ...
}
```

```java
import jakarta.annotation.ManagedBean;
import jakarta.inject.Named;

@ManagedBean
@Named("bike")
class Bike implements Vehicle {
    ...
}
```

#### Disambiguation using a custom qualifier annotation

Another way to disambiguate the injection of a bean is using custom qualifier annotations as follows:

```java
class VehicleRegistry {

    @Inject @Car
    private Vehicle car;

    @Inject @Bike
    private Vehicle bike;
    
    ...
}
```

where the custom annotations are defined as:

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

and the respective beans are declared by using these two _qualifiers_:

```java
import jakarta.annotation.ManagedBean;

@ManagedBean
@Car
class Car implements Vehicle {
    ...
}
```

```java
import jakarta.annotation.ManagedBean;

@ManagedBean
@Bike
class Bike implements Vehicle {
    ...
}
```

## Producer methods

If you want to strictly control the bean generation you can inject your beans using a producer method:

```java
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

class BeanConfiguration {

    @Produces
    @Named("quad")
    @ApplicationScoped
    public Vehicle createVehicle() {
        return new Quad();
    }
}
```

where `Quad` class doesn't have the annotation `@ManagedBean` because the bean is not managed directly by Weld but only through a _producer_ method.

So the bean can be injected this way:

```java
class VehicleRegistry {

    @Inject @Named("car")
    private Vehicle car;

    @Inject @Named("bike")
    private Vehicle bike;

    @Inject @Named("quad")
    private Vehicle quad;
    
    ...
}
```

## Args constructor

Weld takes advantage of the default no-args constructor that a class has to create a bean for that class.
When injecting the bean, it uses reflection to pass the newly created bean to the injected class.
If you don't have a no-args constructor class, or you need some parameters to be injected at creation time you can use `@Inject` onto constructor class **if that class is a bean**

Let's see some example:

```java
import jakarta.annotation.ManagedBean;
import jakarta.inject.Named;

@ManagedBean
@Named("car")
class Car implements Vehicle {
    // it uses the reflection
    @Inject 
    private Engine engine;
}
```

```java
import jakarta.annotation.ManagedBean;
import jakarta.inject.Named;

@ManagedBean
@Named("car")
class Car implements Vehicle {
   
    private final Engine engine;
    
    // it injects into the constructor BECAUSE Car is a BEAN too!!!
    @Inject
    Car(final Engine engine) {
        this.engine = engine;
    }
}
```

If we have a servlet, which is not a bean managed by Weld but it's an instance managed by a servlet container (in our case Tomcat), **that approach is not possible and would return an error**:

```java
import jakarta.inject.Inject;

@WebServlet("/greetz")
public class CarServlet extends HttpServlet {

    private final VehicleService carService;

    // ERROR! @Inject on constructor makes CarServlet a Weld bean which is not true!
    @Inject
    public CarServlet(@Named("carService") final VehicleService carService) {
        this.carService = carService;
    }
}
```