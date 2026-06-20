package org.aiassistant.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public Map<String, String> helloUser() {
        Map<String, String> res = new HashMap<>();
        res.put("email", "mdnu00066@gmail.com");
        res.put("name", "Manoj Dhiman");
        res.put("age", "25");
        return res;
    }
}
