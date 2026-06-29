package org.aiassistant.controllers;

import lombok.AllArgsConstructor;
import org.aiassistant.dtos.UserLoginDTO;
import org.aiassistant.dtos.UserRegisterDTO;
import org.aiassistant.jwt.JwtService;
import org.aiassistant.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterDTO> registerUser(
            @RequestBody UserRegisterDTO userRegisterDTO
            ) {
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        userRegisterDTO.setPassword(encodedPassword);
        UserRegisterDTO savedUserDTO = userService.addUser(userRegisterDTO);
        return new ResponseEntity<>(savedUserDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody UserLoginDTO userLoginDTO
            ) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password
        ));

        Map<String, String> response = new HashMap<>();
        UserDetails userDetails = userService.findUserByUsername(username);
        final String token = jwtService.generateToken(userDetails);
        response.put("token", token);
        response.put("username", username);

        return ResponseEntity.ok(response);
    }

}
