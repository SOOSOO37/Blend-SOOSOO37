package com.blend.server.security.filter;

import com.blend.server.security.jwt.JwtTokenProvider;
import com.blend.server.security.userdetail.CustomUserDetailsService;
import com.blend.server.security.utils.CustomAuthorityUtils;
import com.blend.server.security.utils.UserResignedException;
import com.blend.server.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomAuthorityUtils authorityUtils;

    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            Map<String,Object> claims = verifyJws(request);
            String username = (String) claims.get("username");

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            setAuthenticationToContext(userDetails); // 보안 컨텍스트 설정


        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        }catch (UserResignedException ue) {
            request.setAttribute("exception", ue);
        }catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request,response);
    }

    private Map<String,Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", "");

        String base64SecretKey = jwtTokenProvider.encodeBase64SecretKey(jwtTokenProvider.getSecretKey());
        Map<String,Object> claims = jwtTokenProvider.getClaims(jws, base64SecretKey).getBody();

        return claims;
    }

//        private void setAuthenticationToContext(UserDetails userDetails) {
//        List<GrantedAuthority> authorities = authorityUtils.createAuthorities(Collections.singletonList(userDetails.getAuthorities().toString()));
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }

    private void setAuthenticationToContext(UserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAuthenticationToContext(Map<String,Object> claims) {
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return isValidAuthorization(request);
    }
    private boolean isValidAuthorization(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer");
    }

}
