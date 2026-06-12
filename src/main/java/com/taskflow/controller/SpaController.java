package com.taskflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The React app handles these routes in the browser. When someone
 * opens or refreshes them directly, Spring forwards to index.html
 * so React can take over.
 */
@Controller
public class SpaController {

    @GetMapping({"/login", "/register", "/tasks"})
    public String forwardToSpa() {
        return "forward:/index.html";
    }
}
