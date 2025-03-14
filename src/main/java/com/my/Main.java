package com.my;

import com.my.app.AppFactory;
import com.my.app.ConsoleApp;
import com.my.app.InMemoryAppFactory;

public class Main {
    public static void main(String[] args) {
        AppFactory appFactory = new InMemoryAppFactory();
        ConsoleApp consoleApp = appFactory.createConsoleApp();
        consoleApp.start();
    }
}