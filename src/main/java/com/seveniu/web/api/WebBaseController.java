package com.seveniu.web.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by seveniu on 6/2/16.
 */
@Controller
@RequestMapping("/web-base")
public class WebBaseController {

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String index() {
        return "login";
    }
}
