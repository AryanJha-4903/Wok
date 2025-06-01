package com.Wok.Wok.Config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Wok.Wok.Services.CustomUserDetailService;
import com.Wok.Wok.Services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAutherizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String path = request.getServletPath();
        if(authHeader == null || !authHeader.startsWith("Bearer ") || path.equals("/authenticate") || path.equals("/signup")){
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = authHeader.substring(7);
        System.out.printf("token=> ", jwtToken);
        String username =  jwtService.extractUsername(jwtToken);
        System.out.printf("username=>", username);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
           UserDetails userDetail = customUserDetailService.loadUserByUsername(username);
           if(userDetail != null && jwtService.validateToken(jwtToken, userDetail)){
            UsernamePasswordAuthenticationToken  authenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                userDetail.getPassword(),
                userDetail.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); 
            
           }
        }
        filterChain.doFilter(request, response);

    }

}
