package com.qbook.app.application.filters;

import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.impl.AuthTokenServicesImpl;
import com.qbook.app.domain.models.User;
import com.qbook.app.domain.models.UserPermission;
import com.qbook.app.utilities.CustomHTTPHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

@Log
public class RestFilter implements Filter {
    private final AuthTokenServices authTokenServices;

    @Autowired
    public RestFilter(AuthTokenServices authTokenServices) {
        this.authTokenServices = authTokenServices;
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();
        String authToken;
        log.info("Requested URL -> " + req.getLocalAddr() + ":" + req.getLocalPort() + ((HttpServletRequest) req).getRequestURI());

        String method = request.getMethod();
        // this origin value could just as easily have come from a database
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods","POST, PUT, GET ,OPTIONS ,DELETE");
        response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.setHeader(
                "Access-Control-Allow-Headers",
                "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,X-FORWARDED-FOR");
        if ("OPTIONS".equals(method)) {
            log.info("Pre-flight request to CORS filter");
            response.setStatus(HttpStatus.OK.value());
            chain.doFilter(req, res);
        }  else if(path.contains("/mobile/") && !path.contains("/auth/") && !path.contains("/public/")){
            validateAuthToken("authenticate", request, response);
            chain.doFilter(req, res);
        } else if(path.contains("/auth/") && !method.equals("GET")){
            validateAuthToken("authorise", request, response);
            chain.doFilter(req, res);
        } else if(path.contains("/auth/") && method.equals("GET")) {
            // make sure the user has the right role
//            String authToken = request.getHeader(CustomHTTPHeaders.AUTH_HEADER).substring(7);
//            String authToken = request.getHeader(CustomHTTPHeaders.AUTH_HEADER);
            if(request.getHeader(CustomHTTPHeaders.AUTH_HEADER).startsWith("Bearer")){
                authToken = request.getHeader(CustomHTTPHeaders.AUTH_HEADER).substring(7);
            }
            else {
                authToken = request.getHeader(CustomHTTPHeaders.AUTH_HEADER);
            }

            if(authToken != null && !authToken.equals("")) {
                User user = authTokenServices.extractUser(authToken);

                if (user.getRole().equals("client") && !path.contains("accepted-terms")) {
                    validateReadPermissionForSource(user, request, response);
                } else {
                    validateAuthToken("authorise", request, response);
                }
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            }
            chain.doFilter(req, res);
        } else {
            log.info("Non authorized url accessed.");
            chain.doFilter(req, res);
        }
    }

    private void validateAuthToken(String strategy, HttpServletRequest req, HttpServletResponse response) throws IOException{
        //validate token
        String authToken;
        try {
            log.info( "starting validateAuthToken()");
            if(req.getHeader(CustomHTTPHeaders.AUTH_HEADER).startsWith("Bearer")){
                authToken = req.getHeader(CustomHTTPHeaders.AUTH_HEADER).substring(7);
            }
            else{
                authToken = req.getHeader(CustomHTTPHeaders.AUTH_HEADER);
            }
//            authToken = req.getHeader(CustomHTTPHeaders.AUTH_HEADER);
//            String authToken = req.getHeader(CustomHTTPHeaders.AUTH_HEADER).substring(7);
            Jws<Claims> claims = Jwts.parser().setSigningKey(TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")).parseClaimsJws(authToken);
            Date expirationDate = claims.getBody().getExpiration();
            DateTime timeInMillis = new DateTime(expirationDate.getTime());

            if(timeInMillis.isBeforeNow()){
                //token expired redirect user to login
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            }

            if(strategy.equals("authorise")) { // validate the request is made by a user that has permissions
                User user = this.authTokenServices.extractUser(authToken);
                // any PUT, PATCH, POST, DELETE must be validated if it comes here
                switch (req.getMethod()) {
                    case "POST":
                    case "PUT":
                    case "PATCH":
                    case "DELETE":
                        validateWritePermissionForSource(user, req, response);
                        break;
                }

            }
            log.info( "completing validateAuthToken()");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Auth Filter: Something went wrong while getting auth token", e);
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private void validateWritePermissionForSource(User user, HttpServletRequest req, HttpServletResponse response) throws IOException
    {
        log.info( "starting validateWritePermissionForSource()");

        String path = req.getRequestURI();

        log.info("validateWritePermissionForSource for path " + path + " and method " + req.getMethod());

        boolean userHasWritePermissionForFeature = false;

        for(UserPermission userPermission: user.getUserPermissionList()) {
            if(path.contains(userPermission.getPermissionFeature().toLowerCase())) {
                if(userPermission.isCanWrite()) {
                    userHasWritePermissionForFeature = true;
                }
            }
        }
        if(!userHasWritePermissionForFeature) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }

        log.info( "completing validateWritePermissionForSource()");
    }

    private void validateReadPermissionForSource(User user, HttpServletRequest req, HttpServletResponse response) throws IOException
    {
        log.info( "starting validateWritePermissionForSource()");

        String path = req.getRequestURI();

        log.info("validateWritePermissionForSource for path " + path + " and method " + req.getMethod());

        boolean userHasReadPermissionForFeature = false;

        for(UserPermission userPermission: user.getUserPermissionList()) {
            if(path.contains(userPermission.getPermissionFeature().toLowerCase())) {
                if(userPermission.isCanRead()) {
                    userHasReadPermissionForFeature = true;
                }
            }
        }
        if(!userHasReadPermissionForFeature) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }

        log.info( "completing validateWritePermissionForSource()");
    }
}
