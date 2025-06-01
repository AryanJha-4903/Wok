package com.Wok.Wok.Config;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Abs;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;/
// import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.Wok.Wok.Services.CustomUserDetailService;
import com.Wok.Wok.utils.AuthenticationSuccessHandler;
// import com.Wok.Wok.utils.JwtAuthenticationFilter;

@SuppressWarnings("unused")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  
    @Autowired
    private JwtAutherizationFilter jwtAutherizationFilter;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @SuppressWarnings({ "deprecation" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .cors(cors -> cors.configurationSource(request -> new org.springframework.web.cors.CorsConfiguration().applyPermitDefaultValues()))
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for simplicity (not recommended for production)
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers( "api/signup**","api/authenticate",  "/media/**","/error").permitAll()// Allow access to /test without authentication
                        .requestMatchers("api/admin**").hasRole("ADMIN") // Only allow access to /admin for users with ADMIN role  
                        .requestMatchers("api/user**").hasRole("USER") // Only allow access to /user for users with USER role
                        .requestMatchers("api/h2-console/**").permitAll() // Allow access to H2 console without authentication
                        .anyRequest().authenticated()) // All other requests require authentication
                                    .formLogin(form -> form
                                    .successHandler(new AuthenticationSuccessHandler())
                                    .permitAll()
                                )// Allow form login without authentication
                        .addFilterBefore(jwtAutherizationFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before UsernamePasswordAuthenticationFilter
                .httpBasic(Customizer.withDefaults()); // Use basic authentication
        return http.build();
    }   


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    
    @Bean
    public UserDetailsService userDetailsService(){
        return customUserDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // Use BCryptPasswordEncoder for password encoding  
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager()  {
        return new ProviderManager(authenticationProvider()) ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use a no-op password encoder for simplicity (not recommended for production)
    }
   
}
