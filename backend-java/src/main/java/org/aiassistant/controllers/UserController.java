package org.aiassistant.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.entities.User;
import org.aiassistant.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.findById(id);
    }
}
