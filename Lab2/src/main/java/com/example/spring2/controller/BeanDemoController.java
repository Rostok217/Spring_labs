
package com.example.spring2.controller;

import com.example.spring2.service.BeanDemoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BeanDemoController {

    private final BeanDemoService beanDemoService;

    public BeanDemoController(BeanDemoService beanDemoService) {
        this.beanDemoService = beanDemoService;
    }

    @GetMapping("/beans")
    public String showBeans(Model model) {
        model.addAttribute("result", beanDemoService.checkBeans());
        return "beans";
    }
}
