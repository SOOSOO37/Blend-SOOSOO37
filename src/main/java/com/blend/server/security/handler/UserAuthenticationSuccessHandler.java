package com.blend.server.security.handler;

import com.blend.server.global.response.ErrorResponse;
import com.blend.server.security.dto.LoginResponseDto;
import com.blend.server.user.User;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("# id: {}, Authenticated successfully", authentication.getName());
        sendSuccessResponse(response,authentication);

    }

    private static void sendSuccessResponse(HttpServletResponse response, Authentication authentication) throws  IOException {
        Gson gson = new Gson();
        User user = (User) authentication.getPrincipal(); //사용자 정보 얻기
        LoginResponseDto responseDto = new LoginResponseDto(user.getId(),user.getEmail(),user.getNickName());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(gson.toJson(responseDto));
    }


}



