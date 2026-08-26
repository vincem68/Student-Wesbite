package com.example.student_management.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //provides methods to extract the claims (data) from token
    private final JWTService jwtService;

    private final UserDetailsService userDetailsService;


    //this is where we implement filter logic for requests
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, //request, extract data
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain //chain of responsibility, calls each filter sequentially
    ) throws ServletException, IOException {

        //check here if we have a JWT token within request
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        //check to see if auth header is missing, or it doesn't start with Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            System.out.println("New Request!");
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getServletPath().startsWith("/api/auth")) {
            System.out.println("New Request again!");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("We got the token");

        //now extract token from header
        jwtToken = authHeader.substring(7); //start after "Bearer "
        userEmail = jwtService.extractUsername(jwtToken);

        //check to see if user is not yet connected/authenticated (context doesn't hold data)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){

            //get info from the database through its email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            //if token and user are valid
            if (jwtService.validateToken(jwtToken, userDetails)) {
                //create new token and set user details, credentials (null) and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); //pass the request to next filter after we're done
    }
}
