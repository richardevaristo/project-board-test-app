package com.richardevaristo.projectboardapi.controller;

import com.richardevaristo.projectboardapi.config.jwt.JwtProvider;
import com.richardevaristo.projectboardapi.message.request.LoginRequest;
import com.richardevaristo.projectboardapi.message.request.RegisterRequest;
import com.richardevaristo.projectboardapi.message.response.JwtResponse;
import com.richardevaristo.projectboardapi.message.response.ResponseMessage;
import com.richardevaristo.projectboardapi.model.User;
import com.richardevaristo.projectboardapi.repository.UserRepository;
import com.richardevaristo.projectboardapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return  new ResponseEntity<>(new ResponseMessage("Fail processing request - Username already exists"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return  new ResponseEntity<>(new ResponseMessage("Fail processing request - Email already exists"),
                    HttpStatus.BAD_REQUEST);
        }



        User user = new User(
                request.getFirstname(),
                request.getLastname(),
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
                );

        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("Registration Successful you may now login"), HttpStatus.OK);
    }

    @GetMapping("/user/name")
    @ResponseBody
    public ResponseEntity<?> getUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            return new ResponseEntity<>(new ResponseMessage("You are loggedIn"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("You are not loggedIn"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName() != null ) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            return new ResponseEntity<>(new ResponseMessage("You are now logout"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResponseMessage("You are not loggedIn"), HttpStatus.BAD_REQUEST);
    }
}
