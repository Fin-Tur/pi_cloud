package de.turtle.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.turtle.models.AuthDTOs;
import de.turtle.models.User;
import de.turtle.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCUrrentUser(HttpServletRequest request) {
        String username = getCurrentUser(request);
        User user = userService.findByUsername(username).orElse(null);
        if (username != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTOs.AuthResponse(false, "Not authenticated"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTOs.RegisterRequest request) {
            userService.registerUser(request.getUsername(), request.getPassword());
            logger.info("New user registered successfully");
            
            return ResponseEntity.ok(new AuthDTOs.AuthResponse(true, "User registered successfully"));      
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTOs.LoginRequest request, HttpServletRequest httpRequest) {
            
            boolean authenticated = userService.authenticateUser(request.getUsername(), request.getPassword());
            
            if (authenticated) {
                HttpSession session = httpRequest.getSession(true);
                session.setAttribute("username", request.getUsername());
                session.setAttribute("authenticated", true);
                Optional<User> user = userService.findByUsername(request.getUsername());
                if(user.isPresent()){
                    session.setAttribute("userId", user.get().getId());
                }
                logger.info("A user logged in successfully");
                return ResponseEntity.ok(new AuthDTOs.AuthResponse(true, "Login successful", request.getUsername()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthDTOs.AuthResponse(false, "Invalid username or password"));
            }  
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpRequest) {
            HttpSession session = httpRequest.getSession(false);
            String username = null;
            
            if (session != null) {
                username = (String) session.getAttribute("username");
                session.invalidate(); // Session komplett löschen
            }
            
            logger.info("User logged out successfully: {}", username);
            return ResponseEntity.ok(new AuthDTOs.AuthResponse(true, "Logout successful"));
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication(HttpServletRequest httpRequest) {
            HttpSession session = httpRequest.getSession(false);
            
            if (session != null) {
                String username = (String) session.getAttribute("username");
                Boolean authenticated = (Boolean) session.getAttribute("authenticated");
                
                if (Boolean.TRUE.equals(authenticated) && username != null) {
                    return ResponseEntity.ok(new AuthDTOs.AuthResponse(true, "User is authenticated", username));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDTOs.AuthResponse(false, "Not authenticated"));
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Boolean authenticated = (Boolean) session.getAttribute("authenticated");
            return Boolean.TRUE.equals(authenticated);
        }
        return false;
    }
    
    public static String getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return (String) session.getAttribute("username");
        }
        return null;
    }

    public static Long getCurrentUserId(HttpServletRequest request){
         HttpSession session = request.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return (Long) session.getAttribute("userId");
        }
        return null;
    }
    
}