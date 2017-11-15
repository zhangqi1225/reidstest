package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zhang on 2017/11/11.
 */

@Controller
public class TestController {


    @RequestMapping(value = "/getString")
    public String getString(){
        return "redis get connection";
    }
}
