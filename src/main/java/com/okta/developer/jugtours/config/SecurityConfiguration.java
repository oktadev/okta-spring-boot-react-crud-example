package com.okta.developer.jugtours.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RequestCache requestCache = refererRequestCache();
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setRequestCache(requestCache);
        http
            .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/okta"))
                .and()
            .oauth2Login()
                .successHandler(handler)
                .and()
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
            .requestCache()
                .requestCache(requestCache)
                .and()
            .authorizeRequests()
                .antMatchers("/**/*.{js,html,css}").permitAll()
                .antMatchers("/", "/api/user").permitAll()
                .anyRequest().authenticated();/*
            .and()
                .requiresChannel()
                .requestMatchers(r -> r.getHeader("x-forwarded-proto") != null)
                .requiresSecure();*/
    }

    @Bean
    @Profile("dev")
    public RequestCache refererRequestCache() {
        return new RequestCache() {
            private String savedAttrName = getClass().getName().concat(".SAVED");

            @Override
            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
                String referrer = request.getHeader("referer");
                if (referrer != null) {
                    request.getSession().setAttribute(this.savedAttrName, referrerRequest(referrer));
                }
            }

            @Override
            public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    return (SavedRequest) session.getAttribute(this.savedAttrName);
                }

                return null;
            }

            @Override
            public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
                return request;
            }

            @Override
            public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    log.debug("Removing SavedRequest from session if present");
                    session.removeAttribute(this.savedAttrName);
                }
            }
        };
    }

    private SavedRequest referrerRequest(final String referrer) {
        return new SavedRequest() {
            @Override
            public String getRedirectUrl() {
                return referrer;
            }

            @Override
            public List<Cookie> getCookies() {
                return null;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public List<String> getHeaderValues(String name) {
                return null;
            }

            @Override
            public Collection<String> getHeaderNames() {
                return null;
            }

            @Override
            public List<Locale> getLocales() {
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }
        };
    }
}