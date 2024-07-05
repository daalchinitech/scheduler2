package com.dc.server.scheduler2.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  //WebSecurityConfigurerAdapter
 {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationTokenFilter authenticationTokenFilter;

    @Autowired
    public SecurityConfig (
        CustomAuthenticationEntryPoint authenticationEntryPoint,
        AuthenticationTokenFilter authenticationTokenFilter)
    {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationTokenFilter = authenticationTokenFilter;
    }

   // @Override
//    protected void configure (HttpSecurity http) throws Exception {
//        http.csrf().disable()
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
//            .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
//            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
//            .and()
//            .headers().cacheControl();
//    }
}
