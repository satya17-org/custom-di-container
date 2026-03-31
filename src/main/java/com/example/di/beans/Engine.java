package com.example.di.beans;

import com.example.di.annotations.Component;
import com.example.di.annotations.PostConstruct;
import com.example.di.annotations.PreDestroy;

@Component
public class Engine {
    @PostConstruct
    public void init() {
        System.out.println("--> Engine Lifecycle: Initializing Engine components...");
    }

    public void start() {
        System.out.println("==> Engine operation: Vroom vroom! Engine started.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("--> Engine Lifecycle: Shutting down Engine safely...");
    }
}
