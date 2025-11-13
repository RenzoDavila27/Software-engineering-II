package com.car.clientead.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCodeAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageAttr = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object pathAttr = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Integer statusCode = null;
        if (statusCodeAttr instanceof Integer) {
            statusCode = (Integer) statusCodeAttr;
        } else if (statusCodeAttr != null) {
            try {
                statusCode = Integer.parseInt(statusCodeAttr.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", messageAttr != null ? messageAttr : "Ha ocurrido un error inesperado.");
        model.addAttribute("requestUri", pathAttr != null ? pathAttr : request.getRequestURI());
        return "error.html";
    }
}
