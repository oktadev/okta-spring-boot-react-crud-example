package com.okta.developer.jugtours.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String SAVED_LOGIN_ORIGIN_URI = SecurityConfiguration.class.getName() + "_SAVED_ORIGIN";
    private final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.{js,html,css}");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .requestCache().requestCache(refererRequestCache())
            .and()
                .authorizeRequests()
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
            private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
            private PortResolver portResolver = new PortResolverImpl();

            @Override
            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
                if (request.getRemoteUser() == null && this.requestMatcher.matches(request)) {
                    String referrer = request.getHeader("referer");
                    if (!StringUtils.isEmpty(referrer) &&
                            request.getSession().getAttribute(SAVED_LOGIN_ORIGIN_URI) == null) {
                        log.info("Saving login origin URI: {}", referrer);
                        SavedRequest savedRequest = referrerRequest(referrer);
                        request.getSession().setAttribute(SAVED_LOGIN_ORIGIN_URI, savedRequest);
                    }
                } else {
                    log.debug("Request not saved as configured RequestMatcher did not match");
                }
            }

            @Override
            public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    return (SavedRequest) session.getAttribute(SAVED_LOGIN_ORIGIN_URI);
                }

                return null;
            }

            @Override
            public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
                DefaultSavedRequest saved = (DefaultSavedRequest) getRequest(request, response);

                if (saved == null) {
                    return null;
                }

                if (!saved.doesRequestMatch(request, portResolver)) {
                    log.debug("saved request doesn't match");
                    return null;
                }

                removeRequest(request, response);

                return new SavedRequestAwareWrapper(saved, request);
            }

            @Override
            public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    log.debug("Removing SavedRequest from session if present");
                    session.removeAttribute(SAVED_LOGIN_ORIGIN_URI);
                }
            }
        };
    }

    private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private SavedRequest savedRequest = null;
    /**
     * The set of SimpleDateFormat formats to use in getDateHeader(). Notice that because
     * SimpleDateFormat is not thread-safe, we can't declare formats[] as a static
     * variable.
     */
    protected final SimpleDateFormat[] formats = new SimpleDateFormat[3];

    class SavedRequestAwareWrapper extends HttpServletRequestWrapper {

        SavedRequestAwareWrapper(SavedRequest saved, HttpServletRequest request){
            super(request);
            savedRequest = saved;

            formats[0] = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            formats[1] = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US);
            formats[2] = new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US);

            formats[0].setTimeZone(GMT_ZONE);
            formats[1].setTimeZone(GMT_ZONE);
            formats[2].setTimeZone(GMT_ZONE);
        }
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