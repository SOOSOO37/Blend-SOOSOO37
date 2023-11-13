package com.blend.server.security.config;

import com.blend.server.security.filter.JwtAuthenticationFilter;
import com.blend.server.security.filter.JwtVerificationFilter;
import com.blend.server.security.handler.UserAccessDeniedHandler;
import com.blend.server.security.handler.UserAuthenticationEntryPoint;
import com.blend.server.security.handler.UserAuthenticationFailureHandler;
import com.blend.server.security.handler.UserAuthenticationSuccessHandler;
import com.blend.server.security.jwt.JwtTokenProvider;
import com.blend.server.security.userdetail.CustomUserDetailsService;
import com.blend.server.security.utils.CustomAuthorityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;



@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Getter
    @Value("${config.domain}")
    private String domain;
    private final JwtTokenProvider jwtTokenProvider;

    private final CustomAuthorityUtils authorityUtils;

    private final CustomUserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(Customizer.withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new UserAuthenticationEntryPoint())
                .accessDeniedHandler(new UserAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/admins/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH,"/users/**").hasAnyRole("USER","ADMIN")
                        .antMatchers(HttpMethod.GET,"/users/**").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE,"/users").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH,"/sellers").hasRole("SELLER")
                        .antMatchers(HttpMethod.GET,"/sellers/**").hasRole("SELLER")
                        .antMatchers(HttpMethod.DELETE,"/sellers/**").hasRole("SELLER")
                        .antMatchers(HttpMethod.POST,"/products").hasRole("SELLER")
                        .antMatchers(HttpMethod.PATCH,"/products/**").hasRole("SELLER")
                        .antMatchers(HttpMethod.DELETE,"/products/**").hasRole("SELLER")
                        .antMatchers(HttpMethod.GET,"/orderProducts/**").hasRole("SELLER")
                        .antMatchers(HttpMethod.PATCH,"/orderProducts/**").hasRole("SELLER")
                        .antMatchers(HttpMethod.POST,"/orders").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH,"/orders/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET,"/orders/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET,"/orders").hasRole("USER")
                        .antMatchers(HttpMethod.POST,"/categories").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST,"/carts/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET,"/carts/**").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH,"/carts/**").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE,"/carts/**").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE,"/carts").hasRole("USER")
                        .antMatchers(HttpMethod.GET,"/admins/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH,"/admins/**").hasRole("ADMIN")



                    .anyRequest().permitAll()); // 전부 허가


        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", domain));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer,HttpSecurity>{

        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter =
                    new JwtAuthenticationFilter(authenticationManager,jwtTokenProvider);
            jwtAuthenticationFilter.setFilterProcessesUrl("/users/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter =
                    new JwtVerificationFilter(jwtTokenProvider, authorityUtils,userDetailsService);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}

