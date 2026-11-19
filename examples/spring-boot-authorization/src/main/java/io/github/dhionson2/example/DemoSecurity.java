package io.github.dhionson2.example;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/** Fictional test users and HTTP Basic are for a local demo only; never use this fixture in production. */
@Configuration @EnableMethodSecurity class DemoSecurity {
    @Bean UserDetailsService demoUsers() { return new InMemoryUserDetailsManager(
        User.withUsername("alice").password("{noop}alice-test-password").roles("CUSTOMER").build(),
        User.withUsername("bob").password("{noop}bob-test-password").roles("CUSTOMER").build()); }
    @Bean SecurityFilterChain demoChain(HttpSecurity http) throws Exception {return http.csrf(c->c.disable()).authorizeHttpRequests(a->a.anyRequest().authenticated()).httpBasic(org.springframework.security.config.Customizer.withDefaults()).build();}
}
