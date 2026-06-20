package org.aiassistant.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.UserRegisterDTO;
import org.aiassistant.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterDTO> registerUser(
            @RequestBody UserRegisterDTO userRegisterDTO
            ) {
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        userRegisterDTO.setPassword(encodedPassword);
        UserRegisterDTO savedUserDTO = userService.addUser(userRegisterDTO);
        return new ResponseEntity<>(savedUserDTO, HttpStatus.CREATED);
    }

}
