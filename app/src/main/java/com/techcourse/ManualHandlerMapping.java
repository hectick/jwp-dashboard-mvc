package com.techcourse;

import com.techcourse.controller.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webmvc.org.springframework.web.servlet.mvc.asis.Controller;
import webmvc.org.springframework.web.servlet.mvc.asis.ForwardController;

import java.util.HashMap;
import java.util.Map;
import webmvc.org.springframework.web.servlet.mvc.tobe.HandlerExecution;
import webmvc.org.springframework.web.servlet.mvc.tobe.HandlerMapping;

public class ManualHandlerMapping implements HandlerMapping {

    private static final Logger log = LoggerFactory.getLogger(ManualHandlerMapping.class);

    private final Map<String, HandlerExecution> handlerExecutions = new HashMap<>();

    @Override
    public void initialize() {
        final Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/", new ForwardController("/index.jsp"));
        controllers.put("/login", new LoginController());
        controllers.put("/login/view", new LoginViewController());
        controllers.put("/logout", new LogoutController());
        // controllers.put("/register/view", new RegisterViewController());
        // controllers.put("/register", new RegisterController());

        generateHandlerExecutions(controllers);

        log.info("Initialized ManualHandlerMapping!");
        controllers.keySet()
                .forEach(path -> log.info("Path : {}, Controller : {}", path, controllers.get(path).getClass()));
    }

    private void generateHandlerExecutions(final Map<String, Controller> controllers) {
        for (Entry<String, Controller> entrySet : controllers.entrySet()) {
            handlerExecutions.put(entrySet.getKey(), new ManualHandlerExecution(entrySet.getValue()));
        }
    }

    @Override
    public HandlerExecution getHandler(final HttpServletRequest request) {
        final String requestURI = request.getRequestURI();
        log.debug("Request Mapping Uri : {}", requestURI);
        return handlerExecutions.get(requestURI);
    }
}
