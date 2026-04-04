package com.example.di.context;

import com.example.di.annotations.Autowired;
import com.example.di.annotations.Bean;
import com.example.di.annotations.Component;
import com.example.di.annotations.Configuration;
import com.example.di.annotations.PostConstruct;
import com.example.di.annotations.PreDestroy;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomApplicationContext {
    //when bean is created it is stored in this map
    //key : .class object, Value : actual bean object
    private final Map<Class<?>, Object> beanFactory = new HashMap<>();


    //1 constructor called
    public CustomApplicationContext(Class<?> configClass) {
        try {
            //2. get package of class and then start on calling methods to scan
            String basePackage = configClass.getPackage().getName();
            scanAndInstantiate(basePackage);
            injectDependencies();
            //
            invokePostConstruct();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Custom DI Container", e);
        }
    }

    private void scanAndInstantiate(String basePackage) throws Exception {
        String path = basePackage.replace('.', '/');
        //to getparent folder's url from classloader at given path in target. Enum has only one entry , which is root URL of compiled classes in target for this packege.
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

        // URL (Uniform Resource Locator) is a Java class that represents a reference to a resource, such as a file, directory, or web address. It encapsulates details like the protocol (e.g., file, http, jar), host, path, and other components.
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = Paths.get(resource.toURI()).toFile();
            if (directory.exists() && directory.isDirectory()) {
                scanDirectory(directory, basePackage);
            }
        }
    }

    //scanning all files recursively using java file IO .
    private void scanDirectory(File directory, String packageName) throws Exception {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                //using reflection if this class has annotation @component.
                // If yes, then creating its object and putting it in bean factory map.
                if (clazz.isAnnotationPresent(Component.class)) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beanFactory.put(clazz, instance);
                    System.out.println("[Container] Instantiated Bean: " + clazz.getSimpleName());
                } else if (clazz.isAnnotationPresent(Configuration.class)) {
                    Object configInstance = clazz.getDeclaredConstructor().newInstance();
                    beanFactory.put(clazz, configInstance);
                    System.out.println("[Container] Instantiated Configuration: " + clazz.getSimpleName());
                    
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Bean.class)) {
                            Object beanInstance = method.invoke(configInstance);
                            Class<?> returnType = method.getReturnType();
                            beanFactory.put(returnType, beanInstance);
                            System.out.println("[Container] Instantiated @Bean: " + returnType.getSimpleName());
                        }
                    }
                }
            }
        }
    }

    private void injectDependencies() throws Exception {
        //for all beans in bean factory , check which has @Autowired annotation and then set object's autowired field using reflection
        for (Object bean : beanFactory.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object dependency = beanFactory.get(field.getType());
                    if (dependency == null) {
                        throw new RuntimeException("No bean found of type: " + field.getType().getName() 
                                + " for dependency injection into " + bean.getClass().getSimpleName());
                    }
                    field.setAccessible(true);
                    field.set(bean, dependency);
                    System.out.println("[Container] Injected " + dependency.getClass().getSimpleName() + " into " + bean.getClass().getSimpleName());
                }
            }
        }
    }

    private void invokePostConstruct() throws Exception {
        for (Object bean : beanFactory.values()) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.setAccessible(true);
                    method.invoke(bean);
                    System.out.println("[Container] Invoked @PostConstruct on " + bean.getClass().getSimpleName() + "::" + method.getName());
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(beanFactory.get(clazz));
    }



    //when context is called, what you need to do (like calling pre-destroy etc ) is written here
    public void close() {
        for (Object bean : beanFactory.values()) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean);
                        System.out.println("[Container] Invoked @PreDestroy on " + bean.getClass().getSimpleName() + "::" + method.getName());
                    } catch (Exception e) {
                        System.err.println("Failed to invoke @PreDestroy on " + bean.getClass().getName());
                    }
                }
            }
        }
        beanFactory.clear();
    }
}
