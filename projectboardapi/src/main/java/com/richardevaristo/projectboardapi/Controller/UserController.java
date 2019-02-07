package com.richardevaristo.projectboardapi.Controller;

import com.richardevaristo.projectboardapi.Model.User;
import com.richardevaristo.projectboardapi.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }
}
