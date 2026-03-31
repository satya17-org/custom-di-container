# Custom Dependency Injection Container Project

This plan outlines the creation of a basic Core Java project that demonstrates how Dependency Injection (DI) works under the hood by implementing a custom, lightweight DI container from scratch.

## Proposed Changes

We will create a new Maven project named `custom-di-container` in the scratch directory (extending parallel to the existing `spring-di-demo`).

The project will include:
1. **Custom Annotations**:
   - `@Component`: To mark a class as a bean.
   - `@Autowired`: To mark a field for dependency injection.
   - `@PostConstruct`: To mark a method to be executed after injection (Initialization).
   - `@PreDestroy`: To mark a method to be executed before container shutdown (Destruction).

2. **The DI Container (`CustomApplicationContext`)**:
   - **Scanning**: Scans a specific package for classes with `@Component`.
   - **Instantiation**: Creates instances of the discovered classes.
   - **Injection**: Uses Java Reflection to find fields annotated with `@Autowired` and injects the corresponding bean instances.
   - **Lifecycle Management**: Invokes `@PostConstruct` methods after properties are set, and provides a `close()` method to invoke `@PreDestroy` hooks.

3. **Demonstration Beans**:
   - `Engine` and `Car` to demonstrate injection.
   - Lifecycle tracking within these beans to prove they are instantiated, initialized, and destroyed at the right times.

4. **Main Application**:
   - Bootstraps the `CustomApplicationContext`, retrieves a bean, and uses it, then closes the container.

### Component Structure

#### [NEW] Custom Annotations
`com.example.di.annotations.Component`
`com.example.di.annotations.Autowired`
`com.example.di.annotations.PostConstruct`
`com.example.di.annotations.PreDestroy`

#### [NEW] Container Implementation
`com.example.di.context.CustomApplicationContext`

#### [NEW] Example Beans
`com.example.di.beans.Engine`
`com.example.di.beans.Car`

#### [NEW] Main Application
`com.example.di.CustomDiApplication`

## User Review Required

> [!IMPORTANT]
> - The new project will be created at `c:\Users\satya\.gemini\antigravity\scratch\custom-di-container`. 
> - I will use plain Java Reflection to build the lightweight DI logic (no third-party DI libraries like Spring or Guice will be used).

Please approve this plan, and I will proceed with creating the project and writing the classes.
