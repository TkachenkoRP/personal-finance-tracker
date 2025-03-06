package com.my;

import com.my.app.ConsoleApp;
import com.my.repository.UserRepository;
import com.my.repository.impl.InMemoryUserRepository;
import com.my.service.UserService;
import com.my.service.impl.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        UserService userService = new UserServiceImpl(userRepository);
        ConsoleApp consoleApp = new ConsoleApp(userService);
        consoleApp.start();
    }
}