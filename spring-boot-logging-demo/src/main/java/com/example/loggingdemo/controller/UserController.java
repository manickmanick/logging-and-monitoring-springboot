package com.example.loggingdemo.controller;

import com.example.loggingdemo.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final Counter userCreatedCounter;

    public UserController(MeterRegistry meterRegistry,UserService userService) {
        this.userCreatedCounter = Counter.builder("users.created")
                .description("Number of users created")
                .register(meterRegistry);
        this.userService = userService;
    }

    @GetMapping("/users")
    public String getUser(@RequestParam String name){
        log.info("Received request to get User. name={}",name);
        return userService.getUser(name);
    }

    @PostMapping("/users")
    public String createUser() {

        userCreatedCounter.increment();

        return "User created";
    }
}
