package com.example.loggingdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);

    public String getUser(String name){
        log.debug("Looking up user. name={}",name);
        log.info("user lookup completed. name={}",name);
        return "user: " + name;
    }
}
