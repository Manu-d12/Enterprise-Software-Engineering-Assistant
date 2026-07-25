package org.aiassistant.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.aiassistant.dtos.UserLoginDTO;
import org.aiassistant.dtos.UserRegisterDTO;
import org.aiassistant.entities.User;
import org.aiassistant.jwt.JwtService;
import org.aiassistant.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
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

    @PreAuthorize("hasRole('ADMIN')")
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
            @RequestBody UserLoginDTO userLoginDTO,
            HttpServletResponse res
    ) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password
        ));

        Map<String, String> response = new HashMap<>();
        User userDetails = userService.findUserByUsername(username);
        final String token = jwtService.generateToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true) // if this is false or not set frontend side we can get the cookie using "document.cookie" because it is no longer httpCookie. So it is recommended to make it httpOnly to prevent XSS attacks.
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofHours(2))
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.put("username", username);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie access = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).sameSite("Strict")
                .path("/").maxAge(0).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, access.toString())
                .build();
    }
}
