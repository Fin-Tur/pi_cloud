package de.turtle.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import de.turtle.controller.AuthController;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionAuthenticationFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionAuthenticationFilter.class);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        logger.debug("Processing request: {} {}", method, path);
        
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        if (isProtectedEndpoint(path)) {
            if (!AuthController.isAuthenticated(httpRequest)) {
                logger.warn("Unauthorized access attempt to: {}", path);
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"success\":false,\"message\":\"Authentication required\"}");
                return;
            }
            
            String currentUser = AuthController.getCurrentUser(httpRequest);
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
               path.endsWith("/login.html") ||
               path.endsWith("/login.js") ||
               
               path.equals("/") ||
        path.startsWith("/api/") ||
               path.equals("/favicon.ico");
    }
    
    private boolean isProtectedEndpoint(String path) {
              return path.startsWith("/h2-console/");
    }
}
