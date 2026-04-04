package com.example.di;

import com.example.di.beans.Car;
import com.example.di.context.CustomApplicationContext;

public class CustomDiApplication {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("         Starting Custom DI Container            ");
        System.out.println("=================================================");
        
        CustomApplicationContext context = new CustomApplicationContext(CustomDiApplication.class);

        //till here, beans are created and DI is done.  Now we can use those beans.
        System.out.println("\n-------------------------------------------------");
        System.out.println("                Application Logic                ");
        System.out.println("-------------------------------------------------");
        
        Car car = context.getBean(Car.class);
        if (car != null) {
            car.drive();
        } else {
            System.err.println("Could not retrieve Car bean from Container!");
        }

        com.example.di.beans.Radio radio = context.getBean(com.example.di.beans.Radio.class);
        if (radio != null) {
            radio.play();
        } else {
            System.err.println("Could not retrieve Radio bean from Container!");
        }

        System.out.println("\n=================================================");
        System.out.println("          Shutting Down Custom Container         ");
        System.out.println("=================================================");
        context.close();
    }
}
