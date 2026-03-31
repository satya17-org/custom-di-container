# Walkthrough: Custom Dependency Injection Container

I have successfully created a basic Core Java project demonstrating the fundamental principles of Dependency Injection. 
The project acts as a lightweight, custom-built DI container that mirrors the core functionalities of frameworks like Spring—all without any external libraries besides the Java standard library!

## Summary of Changes

A new Maven project named `custom-di-container` was created with the following implementation:

1. **Custom Annotations**: 
   Introduced standard DI annotations to mark application components and lifecycle phases:
   - `@Component`: Targetting classes.
   - `@Autowired`: Targetting fields for injection.
   - `@PostConstruct` & `@PreDestroy`: Targetting methods for initialization and clean-up hooks.

2. **`CustomApplicationContext` (The Container)**: 
   The heart of the application, utilizing Java Reflection to:
   - **Scan**: Automatically finds all `.class` files in a given package annotated with `@Component`.
   - **Instantiate**: Creates singleton occurrences of the identified components.
   - **Inject Components**: Searches for fields annotated with `@Autowired` and sets their values from the pool of instantiated beans.
   - **Initialisation Lifecycle**: Invokes `@PostConstruct` methods immediately after successful injection.
   - **Destruction Lifecycle**: Exposes a `close()` method to iterate through all beans and run their `@PreDestroy` hooks before exit.

3. **Example Components and Proof of Concept (`CustomDiApplication`)**:
   - `Engine`: A simple component with mock init/start/destroy methods.
   - `Car`: Depends upon `Engine`.
   - Output proves proper behaviour natively mimicking `@Component` scanning, field injection, and lifecycle hooks execution.

## Verification Run

The application successfully compiled and ran natively utilizing Java `1.8`, validating all our mechanisms.

**Execution Output**:

```text
=================================================
         Starting Custom DI Container            
=================================================
[Container] Instantiated Bean: Car
[Container] Instantiated Bean: Engine
[Container] Injected Engine into Car
--> Engine Lifecycle: Initializing Engine components...
[Container] Invoked @PostConstruct on Engine::init
--> Car Lifecycle: Verifying Engine installation, getting Car ready to roll!
[Container] Invoked @PostConstruct on Car::setup

-------------------------------------------------
                Application Logic                
-------------------------------------------------
==> Car operation: Trying to drive...
==> Engine operation: Vroom vroom! Engine started.
==> Car operation: Car is now moving.

=================================================
          Shutting Down Custom Container         
=================================================
--> Engine Lifecycle: Shutting down Engine safely...
[Container] Invoked @PreDestroy on Engine::destroy
--> Car Lifecycle: Parking the Car, releasing resources...
[Container] Invoked @PreDestroy on Car::cleanup
```

> [!TIP]
> **Key Takeaway**
> The Dependency Injection pattern relies purely on **Reflection** and **Inversion of Control** (IoC). Instead of `Car` being responsible for writing `new Engine()`, the `CustomApplicationContext` handles everything from bootstrapping to destruction.
