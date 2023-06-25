package com.blockchainanalysisplatform.Controllers;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller

public class HomeController {

    @GetMapping("/login")
    @ModelAttribute
    public String home(@Param("error") String error, Model model) {
        model.addAttribute("error",error); //TODO:add validation here

        return "login";
    }

}
