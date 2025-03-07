package com.example.macrominder.authService.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {
    @GetMapping("v1/hello")
    public String helloWorld() {
        System.out.println("Hello");
        return "Hello, World!!!";
    }
}
