package com.example.di.beans;

import com.example.di.annotations.Autowired;
import com.example.di.annotations.Component;
import com.example.di.annotations.PostConstruct;
import com.example.di.annotations.PreDestroy;

@Component
public class Car {
    @Autowired
    private Engine engine;

    @PostConstruct
    public void setup() {
        System.out.println("--> Car Lifecycle: Verifying Engine installation, getting Car ready to roll!");
    }

    public void drive() {
        System.out.println("==> Car operation: Trying to drive...");
        if (engine != null) {
            engine.start();
            System.out.println("==> Car operation: Car is now moving.");
        } else {
            System.err.println("==> Car operation error: Engine is NULL! Dependency injection failed.");
        }
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("--> Car Lifecycle: Parking the Car, releasing resources...");
    }
}
