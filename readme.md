# Hardwire
dependency injection without fluff

## overview
the 90% usecase of dependency injection is to wire-up your application on startup.
Nearly every dependency is known on startup and the "dynamic" configuration 
which needed to be done during startup (based on the environment) is also "forseen"
on compiletime.

`Hardwire` tries to shift everything concerning dependency injection into compile-time by 
using annotation-processing. During compilation, it analyzes annotations and 
dependencies and creates a factory that wires everything up as it should be.

## why?
Other DI frameworks rely on e.g. classpath-scanning or registering all dependencies 
before wiring them up using reflection. Also they provide a lot more features like AOP or processing.

Hardwire tries to be the simplest possible solution without magic. You can 
have a look in the generated code to [see how easy it actualy is](/hardwire/src/test/resources/tests/simple/result.java). It uses no reflections at all.

**Faster DI is literally not possible**

## Installation
add on-demand-repository (using https://jitpack.io/)
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```
and the dependency
```
<dependency>
	<groupId>com.github.warmuuh</groupId>
	<artifactId>hardwire</artifactId>
	<version>0.0.3</version>
	<scope>provided</scope>
</dependency>
```

## Getting Started
`Hardwire` uses Modules which contain Singletons that are wired-up.
A basic module definition looks as follows:

```java
@Module
public class DbModule extends DbModuleBase{
	
}
```
The `@Module` annotation triggers the annotation processor which generated the
`DbModuleBase` class. This module creates and wires all objects in this 
and sub-packages. All objects annotated with `@Singleton` are added to this module.

```java
@Singleton
public class Datasource {
	
	@Inject
	DbConfig config;
		
	...
}
```

This Datasource will be created and a DbConfig will be wired in (using the setter 
`setConfig`). 


To run your application, just start it:
```java
public class Application(){
	public static void main(String[] args) {
		DbModule db  = new DbModule();
		db.start();
		
		Datasource ds = db.getDatasource();
	}
}
```

## Features
For examples on how to use these features, have a look at the [tests](/hardwire/src/test/resources/tests).

### Constructor or Fieldbased injection
You can decide if you want to use constructor based or field based injection. Because Hardwire does not use reflection, you have to create setters for fieldbased injection.

### @PostConstruct
Annotating a method with `@PostConstruct` will result in a call at startup.
### Dynamic injection
If you annotate a field with `@Inject` but no assignable `@Singleton` can be found,
an abstract method is created that you are forced to implement, so no "BeanNotFound" 
exceptions on runtime anymore.

The getting started example injects the class DbConfig, but does not declare
a singleton, so actually you are forced to implement that in the module definition:

```java
@Module
public class DbModule extends DbModuleBase{
	@Override
	protected DbConfig createDbConfig(){
	 ...	
	}
}
```

### External injection
 Sometimes, you want to inject e.g. java.sql.Datasource but define it in another module,
 so you have to declare the external declaration at that module:
 ```java
@Module(external="java.sql.Datasource")
public class DbModule extends DbModuleBase{
	@Override
	protected Datasource createDatasource(){
	 ...	
	}
}
```

### inter-module references
Modules can reference other modules, so you can split up your application into
several modules and dependencies are wired in each module.
```java
@Module(imports={"package.ConfigModule"})
public class DbModule extends DbModuleBase{
	
}
```


