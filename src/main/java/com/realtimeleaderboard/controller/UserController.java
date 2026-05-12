package com.realtimeleaderboard.controller;

import com.realtimeleaderboard.model.User;
import com.realtimeleaderboard.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody String username, @RequestBody String password){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.register(username,password));
    }
}
