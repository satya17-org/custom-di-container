package com.example.di.context;

import com.example.di.annotations.Autowired;
import com.example.di.annotations.Component;
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
    private final Map<Class<?>, Object> beanFactory = new HashMap<>();

    public CustomApplicationContext(Class<?> configClass) {
        try {
            String basePackage = configClass.getPackage().getName();
            scanAndInstantiate(basePackage);
            injectDependencies();
            invokePostConstruct();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Custom DI Container", e);
        }
    }

    private void scanAndInstantiate(String basePackage) throws Exception {
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = Paths.get(resource.toURI()).toFile();
            if (directory.exists() && directory.isDirectory()) {
                scanDirectory(directory, basePackage);
            }
        }
    }

    private void scanDirectory(File directory, String packageName) throws Exception {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beanFactory.put(clazz, instance);
                    System.out.println("[Container] Instantiated Bean: " + clazz.getSimpleName());
                }
            }
        }
    }

    private void injectDependencies() throws Exception {
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
