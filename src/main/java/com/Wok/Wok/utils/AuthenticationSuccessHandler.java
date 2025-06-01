package com.Wok.Wok.utils;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(grantAuthority->grantAuthority.getAuthority().equals("ROLE_ADMIN"));
        if(isAdmin){
            setDefaultTargetUrl("/admin");
        }else{
            setDefaultTargetUrl("/user");
        }
        // Redirect to the desired URL after successful authentication
        // getRedirectStrategy().sendRedirect(request, response, "/home"); 
    }
}
