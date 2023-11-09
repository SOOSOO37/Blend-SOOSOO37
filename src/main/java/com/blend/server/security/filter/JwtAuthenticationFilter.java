package com.blend.server.security.filter;

import com.blend.server.admin.Admin;
import com.blend.server.security.dto.LoginRequestDto;
import com.blend.server.security.jwt.JwtTokenProvider;
import com.blend.server.security.utils.UserResignedException;
import com.blend.server.seller.Seller;
import com.blend.server.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto loginDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        //실제 인증 수행
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Object principal = authResult.getPrincipal();

       if(principal instanceof Seller) {
            Seller seller = (Seller) principal;

            String accessToken = delegateSellerAccessToken(seller);
            String refreshToken = delegateSellerRefreshToken(seller);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Refresh", refreshToken);

        }else {
            User user = (User) authResult.getPrincipal();

            String accessToken = delegateAccessToken(user);
            String refreshToken = delegateRefreshToken(user);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Refresh", refreshToken);

            this.getSuccessHandler().onAuthenticationSuccess(request,response,authResult);
        }

    }

    public String delegateSellerAccessToken (Seller seller) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", seller.getEmail());
        claims.put("roles", seller.getRoles());

        String subject = seller.getEmail();
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes());

        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
        String accessToken = jwtTokenProvider.generateAccessToken(claims,subject,expiration,base64SecretKey);

        return accessToken;
    }

    public String delegateSellerRefreshToken(Seller seller){
        String subject = seller.getEmail();
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes());
        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());

        String refreshToken = jwtTokenProvider.generateRefreshToken(subject,expiration,base64SecretKey);

        return refreshToken;
    }

    public String delegateAccessToken (User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes());

        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
        String accessToken = jwtTokenProvider.generateAccessToken(claims,subject,expiration,base64SecretKey);

        return accessToken;
    }

    public String delegateRefreshToken(User user){
        String subject = user.getEmail();
        Date expiration = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes());
        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());

        String refreshToken = jwtTokenProvider.generateRefreshToken(subject,expiration,base64SecretKey);

        return refreshToken;
    }

}
