package com.blockchainanalysisplatform.Configs;

import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> {
            User user = userRepo.findByUsername(username);
            if (user != null) return user;
            user = userRepo.findByEmail(username);
            if (user != null) return user;
            throw new UsernameNotFoundException("User " + username + " not found");
        };
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf) ->
                        csrf
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                )

                //.csrf(withDefaults())
                .authorizeRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/", "/registration", "/login", "/css/**", "/fonts/**", "/images/**", "/js/**", "/vendor/**").permitAll()
                                .anyRequest().hasRole("USER")

                )
                .formLogin((formLogin) ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/checkSubscription")


                )
                .logout((logout) ->
                        logout
                                .logoutSuccessUrl("/")
                );
        return http.build();


    }

}
