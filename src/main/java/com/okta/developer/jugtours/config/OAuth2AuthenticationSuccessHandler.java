package com.okta.developer.jugtours.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.okta.developer.jugtours.config.OAuth2Configuration.SAVED_LOGIN_ORIGIN_URI;

/**
 * AuthenticationSuccessHandler that looks for a saved login origin and redirects to it if it exists.
 */
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
        throws IOException {

        handle(request, response);
        clearAuthenticationAttributes(request);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String targetUrl = determineTargetUrl(request);

        if (response.isCommitted()) {
            log.error("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request) {
        Object savedReferrer = request.getSession().getAttribute(SAVED_LOGIN_ORIGIN_URI);
        if (savedReferrer != null) {
            String savedLoginOrigin = request.getSession().getAttribute(SAVED_LOGIN_ORIGIN_URI).toString();
            log.info("Redirecting to saved login origin URI: {}", savedLoginOrigin);
            request.getSession().removeAttribute(SAVED_LOGIN_ORIGIN_URI);
            return savedLoginOrigin;
        } else {
            return "/";
        }
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}