package com.dc.server.scheduler2.security;


import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
public final class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal (
        @NonNull HttpServletRequest servletRequest,
        @NonNull HttpServletResponse servletResponse,
        FilterChain filterChain) throws ServletException, IOException
    {
        filterChain.doFilter(servletRequest, servletResponse);
    }

//    @Override
//    protected boolean shouldNotFilter (HttpServletRequest request) {
//        String routePath = request.getRequestURI().substring(request.getContextPath().length());
//        String clientId = request.getRemoteAddr();
//        if (routePath.equals(GeneralContext.ROOT)) return true;
//        ThreadContext.put("request.id", request.getHeader(DCConstants.X_TRACE_ID));
//
//        boolean routeOpen = Arrays.stream(ContextFilters.UNGUARDED_PATTERNS).anyMatch(routePath::startsWith);
//
//        log.debug("[shouldNotFilter] route = {}, clientId = {}, routeOpen = {}", routePath, clientId, routeOpen);
//        return routeOpen;
//    }

}
