package com.dc.server.scheduler2.security;

import com.dc.server.common.utility.JsonUtil;
import com.dc.server.web.objects.DCResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.dc.server.common.constants.DCConstants.API_FAILURE;

@Log4j2
@Component
public final class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence (HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
        throws IOException
    {
        DCResponse<Object> failureObj = new DCResponse.Builder<>()
            .status(API_FAILURE)
            .statusCode("GE_1")
            .statusMessage("Invalid credentials")
            .build();

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().println(JsonUtil.toJson(failureObj));

    }
}
