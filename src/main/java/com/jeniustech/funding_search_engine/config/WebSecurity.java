//package com.jeniustech.funding_search_engine.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//public class WebSecurity {
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(List.of(
//                "http://localhost:4200",
//                "http://192.168.208.1:4200"
//        ));
//        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//        corsConfiguration.setAllowedHeaders(List.of("*"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//
//        return source;
//    }
//
//    @Bean
//    SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter()); // to extract roles from jwt token
//
//        httpSecurity
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                        .requestMatchers(HttpMethod.DELETE, "/**").hasAnyRole("manager", "admin")
//                        .requestMatchers(HttpMethod.GET, "/openapi").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/openapi/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/users/anyuser/test").permitAll()
//
////                        customers
//                        .requestMatchers(HttpMethod.GET, "/customers/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/customers/**").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/customers/**").permitAll()
//
//                        .requestMatchers("/error").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/ui-stats/**").permitAll()
//
//////                RestaurantController
////                        .requestMatchers(HttpMethod.POST, "/restaurants").hasAnyRole("manager", "admin")
////                        .requestMatchers(HttpMethod.PUT, "/restaurants").hasAnyRole("manager", "admin")
////                        .requestMatchers(HttpMethod.DELETE, "/restaurants").hasAnyRole("admin")
////
//////                UsersController
////                        .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("admin", "manager")
////
////                // Role tester
////                        .requestMatchers(HttpMethod.GET, "/users/admin/test").hasAnyRole("admin")
////                        .requestMatchers(HttpMethod.GET, "/users/manager/test").hasAnyRole("manager")
////                        .requestMatchers(HttpMethod.GET, "/users/waiter/test").hasAnyRole("waiter")
////                        .requestMatchers(HttpMethod.GET, "/users/customer/test").hasAnyRole("customer")
////                        .requestMatchers(HttpMethod.GET, "/users/blocked/test").hasAnyRole("blocked")
////
////
//////                MenuController
////                        .requestMatchers(HttpMethod.PUT, "/menu").hasAnyRole("manager", "admin")
////                        .requestMatchers(HttpMethod.GET, "/menu/**").permitAll()
////
//////                ReportController
////                        .requestMatchers(HttpMethod.GET, "/restaurants/*/reports").hasAnyRole("manager", "admin")
////
//////                TableController
////                        .requestMatchers(HttpMethod.GET, "/restaurants/*/tables").hasAnyRole("manager", "admin", "waiter")
////                        .requestMatchers(HttpMethod.GET, "/restaurants/*/tables/*/orders").hasAnyRole("manager", "admin", "waiter")
////                        .requestMatchers(HttpMethod.POST, "/restaurants/*/tables").hasAnyRole("manager", "admin", "waiter")
////                        .requestMatchers(HttpMethod.PUT, "/restaurants/*/tables/*").hasAnyRole("manager", "admin", "waiter")
////                        .requestMatchers(HttpMethod.DELETE, "/restaurants/*/tables").hasAnyRole("manager", "admin")
////
//////                OrderController
////                        .requestMatchers(HttpMethod.POST, "/restaurants/*/orders/items").hasAnyRole("customer", "admin", "waiter")
////                        .requestMatchers(HttpMethod.GET, "/restaurants/*/orders/receipt").permitAll()
////                        .requestMatchers(HttpMethod.POST, "/restaurants/*/orders").hasAnyRole("customer", "admin", "waiter")
////                        .requestMatchers(HttpMethod.PUT, "/restaurants/*/orders").hasAnyRole("customer", "admin", "waiter")
////                        .requestMatchers(HttpMethod.DELETE, "/restaurants/*/orders").hasAnyRole("customer", "admin", "waiter")
////
//////                OrderItemController
////                        .requestMatchers(HttpMethod.POST, "/restaurants/*/items").hasAnyRole("customer", "admin", "waiter")
////                        .requestMatchers(HttpMethod.PUT, "/restaurants/*/items").hasAnyRole("customer", "admin", "waiter")
////                        .requestMatchers(HttpMethod.DELETE, "/restaurants/*/items").hasAnyRole("customer", "admin", "waiter")
//
//                        .anyRequest().authenticated())
//                .csrf(AbstractHttpConfigurer::disable)
//                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwt ->
//                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
//        return httpSecurity.build();
//    }
//
//}
